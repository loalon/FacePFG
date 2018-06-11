import sys
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
from PyQt5.QtCore import *
from PyQt5.QtPrintSupport import *
import time


from datetime import datetime
from picamera.array import PiRGBArray
from picamera import PiCamera
import os, shutil
import cognitive_face as CF

import cv2 as cv2
import FCModule.faceCrop as fc
import FCModule.FCTools as FCT
from FCModule.face import Face
import configparser


class App(QDialog):
 
    def __init__(self):
        super().__init__()
        self.title = 'FacePi'
        #self.showMaximized()
        self.left = 50
        self.top = 50
        self.width = 1240
        self.height = 900
        
        self.label = QLabel(self)
        
        #self.textContainer = QLabel(self)
        self.textContainer = QTextEdit(self)
        self.textContainer.setReadOnly(True)
        self.textContainer.setMaximumHeight(300)
        
        self.pixmap=QPixmap(1240,720)
        self.camera=PiCamera() #inicia camara
        self.camera.resolution = (1920,1088) #resolucion
        self.img = fc.captureImage(self.camera)
        self.iniFile='facepi.ini'
        self.font=cv2.FONT_HERSHEY_SIMPLEX #tipo de fuente
        self.config=configparser.ConfigParser()
        #valores por defecto
        self.faceDetector = 'utils/haarcascade_frontalface_alt2.xml' #filtro Haar
        self.sKey = '6ff97ccedba642f78dc07a821122fc4d'  # Clave de subscripción.
        self.server = 'northeurope' #servidor
        self.groupName = "conocidos" #grupo de personas
        self.waitTime = 20 #segundos entre detecciones


        self.reconBtn=QPushButton("Reconocer")
        self.reconBtn.setFixedWidth(100)
        self.reconBtn.clicked.connect(self.identify)
        self.reconBtn.setEnabled(False)

        CF.Key.set(self.sKey)
        CF.BaseUrl.set('https://'+self.server+'.api.cognitive.microsoft.com/face/v1.0/')

        ### COMPROBACION DIRECTORIOS
        if not os.path.exists('temp'):
           os.makedirs('temp')
        #if not os.path.exists('img'):
           #os.makedirs('img')
           
        self.readConfig(self.iniFile)
        self.initUI()
        
    def initUI(self):
        self.setWindowTitle(self.title)

        #exitAction = QAction("Salir", self)
        #exitAction.triggered.connect(self.appExit)

        #mainMenu = self.menuBar()
        #fileMenu = mainMenu.addMenu('Principal')
        #fileMenu.addAction(exitAction)
        
        self.setGeometry(self.left, self.top, self.width, self.height)
        self.setFixedSize(self.width, self.height)
        #self.setCentralWidget()
        self.createGridLayout()
        windowLayout = QVBoxLayout()
        windowLayout.addWidget(self.horizontalGroupBox)
        self.setLayout(windowLayout)
 
        self.show()
        self.printC('Iniciando FacePi')

    def createGridLayout(self):
        self.horizontalGroupBox = QGroupBox()
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
         
        #textContainer = QLabel(self)
        #self.textContainer.setText("Probando")
        split1.addWidget(self.textContainer) 
        layout.addWidget(split1)
 
        self.horizontalGroupBox.setLayout(layout)
 
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
          
    def getKey(self):
        dlg=QInputDialog(self)
        dlg.setFixedSize(1000,1000)
        key, okp = dlg.getText(self, "Configuracion", "Clave: ", QLineEdit.Normal, self.sKey)
        if okp and key != '':
            print (key)
            return key, True
        else:
            return "", False

    def getServer(self):
        key, okp = QInputDialog.getText(self, "Configuracion", "Servidor: ", QLineEdit.Normal, self.server)
        if okp and key != '':
            print (key)
            return key, True
        else:
            return "", False
    def getGroup(self):
        key, okp = QInputDialog.getText(self, "Configuracion", "Grupo: ", QLineEdit.Normal, self.groupName)
        if okp and key != '':
            print (key)
            return key, True
        else:
            return "", False
        
    def closeEvent(self, event):
        #print('Pulsando X')
        self.appExit()
        #self.deleteLater()
        #print('Pulsando X')
        

    def getTimestamp(self):
       now=datetime.now()
       nowStr=str(now.year)+str(now.month)+str(now.day)+'_'+str(now.hour)+str(now.minute)+str(now.second)
       return nowStr
    
    def capture(self):
        now=datetime.now()
        self.printC("Capturando imagen: "+str(now))
        self.img = fc.captureImage(self.camera)
        cv2.imwrite('temp/temp.jpg', self.img)
        self.pixmap=QPixmap('temp/temp.jpg')
        self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
        self.label.setPixmap(self.pixmap)

        self.botonAuto.setEnabled(True)

    def identify(self):
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

        #if personDetected is True:
            
            #self.printC("Guardando imagen")
            #cv2.imwrite('img/image'+nowStr+'.jpg', self.img)
            #cv2.imwrite('temp/temp.jpg', self.img)
            #self.printC("Esperando "+str(self.waitTime)+ " segundos para siguiente ejecución")
            #time.sleep(self.waitTime)
            #self.label.setPixmap('temp/temp.jpg')
            #return True
        self.pixmap=QPixmap('temp/temp.jpg')
        self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
        self.label.setPixmap(self.pixmap) 


    def printC(self, text):
        msg = self.getTimestamp()+" " + text + '\n'
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
        if not path:
            return
        try:
            #with open(path, 'w') as f:
            print(path)
            self.img=cv2.imread(path)
            self.pixmap=QPixmap(path)
            self.pixmap=self.pixmap.scaled(1024,720,Qt.KeepAspectRatio)
            self.label.setPixmap(self.pixmap)
            self.botonAuto.setEnabled(True)
            
        except Exception as e:
            self.alertDialog(str(e))

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
    
    """      
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
     """       


 
 
if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = App()
    sys.exit(app.exec_())
