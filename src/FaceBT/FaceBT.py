import os
import subprocess
import select
import serial
import time
import json
import re
import sys

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

camera= PiCamera()

class FaceBT:
    def __init__(self):

        if not os.path.exists('temp'):
            os.makedirs('temp')
    
        self.port = serial.Serial("/dev/rfcomm0", baudrate=9600, timeout=1)
        self.camera=camera #inicia camara
        self.camera.resolution = (1920,1088) #resolucion

        self.font=cv2.FONT_HERSHEY_SIMPLEX #tipo de fuente
        self.config=configparser.ConfigParser()
        self.config.read("FaceBT.ini")
        self.cfg=self.config['CONFIG']
        self.fn=self.cfg['facedetector']
        self.faceDetector = os.path.join(os.path.dirname(__file__), self.fn)
        self.sKey=self.cfg['skey']
        self.server=self.cfg['server']
        self.groupName=self.cfg['groupname']

        CF.Key.set(self.sKey)
        CF.BaseUrl.set('https://'+self.server+'.api.cognitive.microsoft.com/face/v1.0/')
 
    def readSerial(self):
        res = self.port.read(50).decode("utf-8")
        if len(res):
            return res.splitlines()
        else:
            return []
 
    #def send_serial(self, text):
        #self.port.write(bytes(text, 'UTF-8'))

    def sendSerial(self, text):
        self.port.write(bytes(text +" \n", 'UTF-8'))
        
    #def printSerial(self, text):
       #self.port.write(text)
    """
    def readExecuteSend(self, shell, ble_comm, ble_line):

        json_object = json.loads(ble_line)
        ip_address = ble_comm.wifi_connect(json_object['SSID'], json_object['PWD'])
        if ip_address == "<Not Set>":
            print("Fail to connect to Internet")
            #send back fail to configure wifi
            callback_message = {'result': "FAIL", 'IP': ip_address}
            callback_json = json.dumps(callback_message)
            ble_comm.send_serial(callback_json)
            return False
        
        else:
            #isConnected = True
            print("connect to Internet! your ip_address: " + ip_address)
            #send back configure wifi succesfully
            callback_message = {'result': "SUCCESS", 'IP': ip_address}
            callback_json = json.dumps(callback_message)
            ble_comm.send_serial(callback_json)

            return True
    """      
    def identify(self, mode):
        
        #if (mode == "text"):

        #elif (mode == "json"):

        #elif (mode == "image"):
        
        self.sendSerial("Iniciando identificacion, espere por favor ")

        while True:
            #now=datetime.now()
            self.sendSerial("\nEjecutando: ")
            #nowStr='_'+str(now.year)+str(now.month)+str(now.day)+'_'+str(now.hour)+str(now.minute)+str(now.second)
            img = fc.captureImage(self.camera)
            self.sendSerial("Imagen capturada ")
            faceList=fc.faceDetect(self.faceDetector, img, cv2) #detecta
            jsonList={}
            #finalImage=None
            #files=[]
            personDetected = False
            if len(faceList) == 0:
                self.sendSerial('No se han detectado caras')
                if (mode == "json"):
                    print("json=??")
                    self.sendSerial('<<<{}>>>')
                return # vuelve al principio sin esperar
            else:
                self.sendSerial('Cara/s detectada/s')
                fc.faceCrop(img, cv2, faceList) #si hay caras se recortan
            
            for face in faceList:
                isPerson=FCT.identifyFace(CF, self.groupName, face)
                if isPerson is True: #no debe ser un objeto
                    personDetected = True
                    FCT.paintImage(cv2, self.font, img, face)
                    jsonList[face.name]=face.confidence
                    #jsonList[face.name]['confidence']=face.confidence
                    
                    if (mode=="text"):
                        self.sendSerial("Sujeto "+face.name)
                        self.sendSerial("Confianza "+str(face.confidence))
                        
                   
            if personDetected is True:
                if (mode == "image"):
                    cv2.imwrite('temp/temp.jpg', img)
                    self.port.write(open('temp/temp.jpg', 'rb').read())
                elif (mode == "json"):
                    jsonString= json.dumps(jsonList)
                    self.sendSerial("<<<"+jsonString+">>>")
                return

            else:
                if (mode == "json"):
                    self.sendSerial('{}')
                else:
                    self.sendSerial('No se han identificado caras')
                return
            

 
def main():
    FBT = None
    isConnected = False
    
    while True:
        try:
            FBT = FaceBT()
            serialInput = FBT.readSerial()

            for line in serialInput:
                print("Comando: " +str(line))
                if line == "getTexto":
                    FBT.sendSerial("Modo envio: texto")
                    FBT.identify("text")
                    time.sleep(2)
                elif line == "getJSON":
                    FBT.sendSerial("Modo envio: JSON")
                    FBT.identify("json")
                    time.sleep(2)
                elif line == "getImagen":
                    FBT.sendSerial("Modo envio: imagen")
                    FBT.identify("image")
                    time.sleep(2)
                else: 
                    FBT.sendSerial("Comando no valido")
                FBT.sendSerial("Esperando comando")
            
        except serial.SerialException:
            print("Esperando conexion")
            FBT = None
            isConnected = False
            time.sleep(1)
        except KeyboardInterrupt: #Ctrl+C finaliza el bucle
            camera.close()
            pass
        except:
            FBT = FaceBT()
            FBT.sendSerial("Error. Revise terminal de Raspberry Pi")
            print("Error", sys.exc_info()[0])

            
if __name__ == "__main__":
    main()
