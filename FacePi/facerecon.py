import sys
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
from PyQt5.QtCore import *
from PyQt5.QtPrintSupport import *
import time


from datetime import datetime
from picamera.array import PiRGBArray
from picamera import PiCamera
import os
import cognitive_face as CF

import cv2 as cv2
import FCModule.faceCrop as fc
import FCModule.FCTools as FCT
from FCModule.face import Face
import configparser


class App(QDialog):
 
    def __init__(self):
        super().__init__()
        self.title = 'FaceRecon'
        #self.showMaximized()
        self.left = 10
        self.top = 10
        self.width = 1024
        self.height = 720
        
        self.label = QLabel(self)
        
        #self.textContainer = QLabel(self)
        self.textContainer = QTextEdit(self)
        self.textContainer.setReadOnly(True)
        
        self.pixmap=QPixmap(1024,720)
        self.camera=PiCamera() #inicia camara
        self.camera.resolution = (1920,1088) #resolucion
        self.img = fc.captureImage(self.camera)
        self.iniFile='facerecon.ini'
        self.font=cv2.FONT_HERSHEY_SIMPLEX #tipo de fuente
        self.config=configparser.ConfigParser()
        #valores por defecto
        self.faceDetector ='utils/haarcascade_frontalface_alt2.xml' #filtro Haar
        self.sKey = '6ff97ccedba642f78dc07a821122fc4d'  # Clave de subscripción.
        self.server = 'northeurope' #servidor
        self.groupName="conocidos" #grupo de personas
        self.waitTime=20 #segundos entre detecciones

        self.isLoop=False

        CF.Key.set(self.sKey)
        CF.BaseUrl.set('https://'+self.server+'.api.cognitive.microsoft.com/face/v1.0/')

        ### COMPROBACION DIRECTORIOS
        if not os.path.exists('temp'):
           os.makedirs('temp')
        if not os.path.exists('img'):
           os.makedirs('img')
           
        self.readConfig(self.iniFile)
        self.initUI()

    def readConfig(self, iniFile):
        self.config.read(iniFile)
        cfg=self.config['CONFIG']
        self.faceDetector=cfg['facedetector']
        self.sKey=cfg['skey']
        self.server=cfg['server']
        self.groupName=cfg['groupname']
        self.waitTime=int(cfg['waittime'])

    def saveConfig(self, iniFile):
        with open(iniFile, 'w') as configfile:
            self.config.write(configfile)
 
    def initUI(self):
        self.setWindowTitle(self.title)
        self.setGeometry(self.left, self.top, self.width, self.height)
        self.setFixedSize(self.width, self.height)
        self.createGridLayout()
 
        windowLayout = QVBoxLayout()
        windowLayout.addWidget(self.horizontalGroupBox)
        self.setLayout(windowLayout)
 
        self.show()
        self.printC('Iniciando faceRecon')
        #while(1):
            #time.sleep(5)
            #self.pixmap = QPixmap('2.jpg')
            #self.createGridLayout()
            #time.sleep(5)
            #self.pixmap = QPixmap('2.jpg')
            #self.createGridLayout()

    def getTimestamp(self):
       now=datetime.now()
       nowStr='_'+str(now.year)+str(now.month)+str(now.day)+'_'+str(now.hour)+str(now.minute)+str(now.second)
       return nowStr
    
    def capture(self):
        now=datetime.now()
        self.printC("\nEjecutando: "+str(now))
        self.img = fc.captureImage(self.camera)
        faceList=fc.faceDetect(self.faceDetector, self.img, cv2) #detecta
        #finalImage=None
        #files=[]
        personDetected = False
        if len(faceList) == 0:
            self.printC('No se han detectado caras')
            cv2.imwrite('temp/temp.jpg', self.img)
            return # vuelve al principio sin esperar
        else:
            self.printC('Cara/s detectada/s')
            fc.faceCrop(self.img, cv2, faceList) #si hay caras se recortan
        
        for face in faceList:
            isPerson=FCT.identifyFace(CF, self.groupName, face)
            if isPerson is True: #no debe ser un objeto
                personDetected = True
                FCT.paintImage(cv2, self.font, self.img, face)
                self.printC("Sujeto "+face.name)
        cv2.imwrite('temp/temp.jpg', self.img)
        if personDetected is True:
            
            #self.printC("Guardando imagen")
            #cv2.imwrite('img/image'+nowStr+'.jpg', self.img)
            #cv2.imwrite('temp/temp.jpg', self.img)
            #self.printC("Esperando "+str(self.waitTime)+ " segundos para siguiente ejecución")
            #time.sleep(self.waitTime)
            #self.label.setPixmap('temp/temp.jpg')
            return True

    def captureLoop(self):
        self.printC('hola')
        """
        if self.isLoop is False:
            while True:
                self.isLoop = True
                self.prueba()
                time.sleep(self.waitTime)
        else:
            self.isLoop = False
            self.textContainer.setText("Bucle parado")
            return
        """
    def printC(self, text):
        msg = text + '\n'
        self.textContainer.insertPlainText(msg)
        self.textContainer.moveCursor(QTextCursor.End)

    def clearC(self):
        self.textContainer.clear()

    def alertDialog(self, msg):
        ad = QMessageBox(self)
        ad.setText(msg)
        ad.setIcon(QMessageBox.Critical)
        ad.show()

    def saveImage(self):
        dialog=QFileDialog()
        dialog.setDefaultSuffix('jpg')
        path, _ = dialog.getSaveFileName(self, "Salvar imagen", "", "Imagenes (*.jpg)")
        if not path:
            return
        try:
            #with open(path, 'w') as f:
            print(path)
            cv2.imwrite((path+'.jpg'), self.img)
        except Exception as e:
            self.alertDialog(str(e))

    def loadImage(self):
        path, _ = QFileDialog().getOpenFileName(self, "Cargar imagen", "", "Imagenes (*.jpg)")
        #if not path:
            #return
        try:
            #with open(path, 'w') as f:
            print(path)
            self.img=cv2.imread(path)
            self.pixmap=QPixmap(path)
            self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
            self.label.setPixmap(self.pixmap)
            
        except Exception as e:
            self.alertDialog(str(e))

    
    
        
    def prueba(self):
        #self.printC("Exito")
        #self.pixmap=QPixmap('1.jpeg')
        self.pixmap=self.pixmap.scaled(720,720,Qt.KeepAspectRatio)
        self.label.setPixmap(self.pixmap)
        personDetected=self.capture()
        self.pixmap=QPixmap('temp/temp.jpg')
        self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
        self.label.setPixmap(self.pixmap)      
        if personDetected:
            self.printC("Cara detectada")
        else:
            self.printC("No se han detectado caras")
            

    def createGridLayout(self):
        self.horizontalGroupBox = QGroupBox("Grid")
        layout = QVBoxLayout()
        #layout.setColumnStretch(1, 4)
        #layout.setColumnStretch(2, 4)
        #label = QLabel(self)
        #pixmap = QPixmap('2.jpg')
        split1=QSplitter(Qt.Vertical)

        self.label.setPixmap(self.pixmap)
        split1.addWidget(self.label) 
        #layout.addStretch()

        split2=QSplitter(Qt.Horizontal)
        boton=QPushButton("reread")
        boton.setFixedWidth(100)
        boton.clicked.connect(self.prueba)
        split2.addWidget(boton)

        botonAuto=QPushButton("loop")
        botonAuto.setFixedWidth(100)
        botonAuto.clicked.connect(self.captureLoop)
        split2.addWidget(botonAuto)

        clearBtn=QPushButton("Limpiar")
        clearBtn.setFixedWidth(100)
        clearBtn.clicked.connect(self.clearC)
        split2.addWidget(clearBtn)
       

        saveBtn=QPushButton("Salvar")
        saveBtn.setFixedWidth(100)
        saveBtn.clicked.connect(self.saveImage)
        split2.addWidget(saveBtn)

        saveBtn=QPushButton("Cargar")
        saveBtn.setFixedWidth(100)
        saveBtn.clicked.connect(self.loadImage)
        split2.addWidget(saveBtn)

        split1.addWidget(split2)
         
        #textContainer = QLabel(self)
        self.textContainer.setText("Probando")
        split1.addWidget(self.textContainer) 
        layout.addWidget(split1)
 
        self.horizontalGroupBox.setLayout(layout)
 
 
 
if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = App()
    sys.exit(app.exec_())
