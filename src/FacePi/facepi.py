#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: FacePi.py
Description: Aplicacion de deteccion de caras con GUI
Author: Alonso Serrano
Created: 180529
Version: 180613
"""

import sys
import time
import configparser
from datetime import datetime
import os, shutil
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
from PyQt5.QtCore import *
from PyQt5.QtPrintSupport import *

from picamera.array import PiRGBArray
from picamera import PiCamera
import cognitive_face as CF
import cv2 as cv2

import FCModule.faceCrop as fc
import FCModule.FCTools as FCT
from FCModule.face import Face

## Clase principal de FacePi
#
# Genera el GUI y contiene funciones para la gestion del interfaz. 
# Contiene funciones para la identificacion de rostros mediante captura o
# carga de imagenes
class FacePi(QDialog):
    ## Contructor
    def __init__(self):
        super().__init__()
        ## @var title
        # titulo de la aplicacion 
        self.title = 'FacePi'
        ## @var left
        # Posicion izquierda de la aplicacion 
        self.left = 50
        ## @var top
        # Posicion superior de la aplicacion 
        self.top = 50
        ## @var width
        # Ancho de la aplicacion 
        self.width = 1240
        ## @var height
        # Alto de la aplicacion 
        self.height = 900
        ## @var label
        # Contenedor de objetos         
        self.label = QLabel(self)
        ## @var textContainer
        # Contenedor de texto de consola 
        self.textContainer = QTextEdit(self)
        self.textContainer.setReadOnly(True)
        self.textContainer.setMaximumHeight(300)
        ## @var pixmap
        # Contenedor de imagenes        
        self.pixmap=QPixmap(1240,720)
        ## @var camera
        # Objeto de PiCamera 
        self.camera=PiCamera() 
        self.camera.resolution = (1920,1088) 
        ## @var img
        # Guarda la imagen mostrada en pantalla
        self.img = fc.captureImage(self.camera)
        ## @var iniFile
        # Archivo de configuracion
        self.iniFile=os.path.join(os.path.dirname(__file__), 'facepi.ini')
        ## @var font
        # Fuente para el rotulado de caras
        self.font=cv2.FONT_HERSHEY_SIMPLEX
        ## @var config
        # Objeto que contiene parametros de configuracion
        self.config=configparser.ConfigParser()
        
        #valores por defecto
        
        ## @var faceDetector
        # Ubicacion del filtro Haar
        self.faceDetector = 'utils/haarcascade_frontalface_alt2.xml' 
        ## @var sKey
        # Clave de suscripcion de Azure
        self.sKey = '6ff97ccedba642f78dc07a821122fc4d'  
        ## @var server
        # Servidor de Azure
        self.server = 'northeurope' 
        ## @var groupName
        # Grupo de personas de Azure
        self.groupName = "conocidos" 
        ## @var waitTime
        # Tiempo de espera
        self.waitTime = 20 
        ## @var reconBtn
        # Boton de reconocimiento 
        self.reconBtn=QPushButton("Reconocer")
        self.reconBtn.setFixedWidth(100)
        self.reconBtn.clicked.connect(self.identify)
        self.reconBtn.setEnabled(False)

        CF.Key.set(self.sKey)
        CF.BaseUrl.set('https://'+self.server+'.api.cognitive.microsoft.com/face/v1.0/')

        if not os.path.exists('temp'):
           os.makedirs('temp')
           
        self.readConfig(self.iniFile)
        self.initUI()
    
    ## Genera el interfaz grafico.
    def initUI(self):
        self.setWindowTitle(self.title)
        self.setGeometry(self.left, self.top, self.width, self.height)
        self.setFixedSize(self.width, self.height)
        self.createGridLayout()
        windowLayout = QVBoxLayout()
        windowLayout.addWidget(self.horizontalGroupBox)
        self.setLayout(windowLayout)
        self.show()
        self.printC('Iniciando FacePi')
        
    ## Crea la disposicion de objetos en gradilla.
    def createGridLayout(self):
        self.horizontalGroupBox = QGroupBox()
        layout = QVBoxLayout()
        split1=QSplitter(Qt.Vertical)

        self.label.setPixmap(self.pixmap)
        split1.addWidget(self.label) 

        split2=QSplitter(Qt.Horizontal)
        captureBtn=QPushButton("Capturar")
        captureBtn.setFixedWidth(100)
        captureBtn.clicked.connect(self.capture)
  
        clearBtn=QPushButton("Limpiar")
        clearBtn.setFixedWidth(100)
        clearBtn.clicked.connect(self.clearC)

        saveBtn=QPushButton("Salvar")
        saveBtn.setFixedWidth(100)
        saveBtn.clicked.connect(self.saveImage)

        loadBtn=QPushButton("Cargar")
        loadBtn.setFixedWidth(100)
        loadBtn.clicked.connect(self.loadImage)

        cfgBtn=QPushButton("Configuracion")
        cfgBtn.setFixedWidth(100)
        cfgBtn.clicked.connect(self.configApp)
        
        split2.addWidget(captureBtn)
        split2.addWidget(loadBtn)
        split2.addWidget(self.reconBtn)
        split2.addWidget(saveBtn)
        
        split2.addWidget(clearBtn)    
        split2.addWidget(cfgBtn)
        
        split1.addWidget(split2)
        split1.addWidget(self.textContainer) 
        layout.addWidget(split1)
 
        self.horizontalGroupBox.setLayout(layout)
    
    ## Lee la configuracion del sistema.
    # @param iniFile Archivo de configuracion
    def readConfig(self, iniFile):
        self.config.read(iniFile)
        cfg=self.config['CONFIG']
        fn=cfg['facedetector']
        self.faceDetector = os.path.join(os.path.dirname(__file__), fn)
        self.sKey=cfg['skey']
        self.server=cfg['server']
        self.groupName=cfg['groupname']
        self.waitTime=int(cfg['waittime'])
    
    ## Salva la configuracion al archivo de configuracion.
    # @param iniFile Archivo de configuracion
    def saveConfig(self, iniFile):
        with open(iniFile, 'w') as configfile:
            self.config.write(configfile)
    
    ## Carga los dialogos de configuracion y salva los parametros.
    def configApp(self):
        key, keyOk= self.getKey()
        if not keyOk:
            self.alertDialog("No se han salvado cambios")
            return
        server, serverOk= self.getServer()
        if not serverOk:
            self.alertDialog("No se han salvado cambios")
            return
        group, groupOk= self.getGroup()
        if not groupOk:
            self.alertDialog("No se han salvado cambios")
            return
        
        if keyOk and serverOk and groupOk:
            self.config['CONFIG']['skey'] = key
            self.config['CONFIG']['server'] = server
            self.config['CONFIG']['groupname'] = group
            self.saveConfig(self.iniFile)
            self.groupName = group
            self.server = server
            self.sKey = key
            CF.Key.set(self.sKey)
            CF.BaseUrl.set('https://'+self.server+'.api.cognitive.microsoft.com/face/v1.0/')
        else:
            self.alertDialog("No se han salvado cambios")
    
    ## Dialogo para solicitud de clave.
    def getKey(self):
        dlg=QInputDialog(self)
        dlg.setFixedSize(1000,1000)
        key, okp = dlg.getText(self, "Configuracion", "Clave: ", QLineEdit.Normal, self.sKey)
        if okp and key != '':
            print (key)
            return key, True
        else:
            return "", False
    
    ## Dialogo para solicitud de servidor.
    def getServer(self):
        key, okp = QInputDialog.getText(self, "Configuracion", "Servidor: ", QLineEdit.Normal, self.server)
        if okp and key != '':
            print (key)
            return key, True
        else:
            return "", False
    
    ## Dialogo para solicitud de grupo.
    def getGroup(self):
        key, okp = QInputDialog.getText(self, "Configuracion", "Grupo: ", QLineEdit.Normal, self.groupName)
        if okp and key != '':
            print (key)
            return key, True
        else:
            return "", False
        
    
    ## Lee el puerto de serie Bluetooth.
    # @return nowStr String con la marca temporal.
    def getTimestamp(self):
       now=datetime.now()
       nowStr=str(now.year)+str(now.month)+str(now.day)+'_'+str(now.hour)+str(now.minute)+str(now.second)
       return nowStr
    
    ## Captura una imagen.
    def capture(self):
        now=datetime.now()
        self.printC("Capturando imagen: "+str(now))
        self.img = fc.captureImage(self.camera)
        cv2.imwrite('temp/temp.jpg', self.img)
        self.pixmap=QPixmap('temp/temp.jpg')
        self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
        self.label.setPixmap(self.pixmap)
        self.reconBtn.setEnabled(True)
    
    ## Identifica rostros.
    def identify(self):
        faceList=fc.faceDetect(self.faceDetector, self.img, cv2) 
        personDetected = False
        if len(faceList) == 0:
            self.printC('No se han detectado caras')
            cv2.imwrite('temp/temp.jpg', self.img)
            return 
        else:
            self.printC('Cara/s detectada/s')
            fc.faceCrop(self.img, cv2, faceList) 
        
        for face in faceList:
            isPerson=FCT.identifyFace(CF, self.groupName, face)
            if isPerson is True: 
                personDetected = True
                FCT.paintImage(cv2, self.font, self.img, face)
                self.printC("Sujeto "+face.name)
        cv2.imwrite('temp/temp.jpg', self.img)

        self.pixmap=QPixmap('temp/temp.jpg')
        self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
        self.label.setPixmap(self.pixmap) 

    
    ## Escribe un mensaje en la consola.
    # @param text
    def printC(self, text):
        msg = self.getTimestamp()+" " + text + '\n'
        self.textContainer.insertPlainText(msg)
        self.textContainer.moveCursor(QTextCursor.End)
    
    ## Limpia la consola.
    def clearC(self):
        self.textContainer.clear()
    
    ## Genera un cuadro de alerta.
    # @param msg Texto del mensaje de alerta
    def alertDialog(self, msg):
        ad = QMessageBox(self)
        ad.setText(msg)
        ad.setIcon(QMessageBox.Critical)
        ad.show()
    
    ## Salva la imagen mediante cuadro de seleccion de archivos.
    def saveImage(self):
        dialog=QFileDialog()
        dialog.setDefaultSuffix('jpg')
        path, _ = dialog.getSaveFileName(self, "Salvar imagen", "", "Imagenes (*.jpg)")
        if not path:
            return
        try:
            print(path)
            cv2.imwrite((path+'.jpg'), self.img)
        except Exception as e:
            self.alertDialog(str(e))
    
    ## Carga imagen mediante cuadro de seleccion de archivos.
    def loadImage(self):
        path, _ = QFileDialog().getOpenFileName(self, "Cargar imagen", "", "Imagenes (*.jpg)")
        if not path:
            return
        try:
            self.img=cv2.imread(path)
            self.pixmap=QPixmap(path)
            self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
            self.label.setPixmap(self.pixmap)
            self.reconBtn.setEnabled(True)
            
        except Exception as e:
            self.alertDialog(str(e))
    
    ## Gestiona el cierre de la aplicacion.
    def appExit(self):
        folder = './temp'
        for file in os.listdir(folder):
            path = os.path.join(folder, file)
            try:
                if os.path.isfile(path):
                    os.unlink(path)
            except Exception as e:
                self.alertDialog(str(e))
        sys.exit()

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = FacePi()
    sys.exit(app.exec_())
