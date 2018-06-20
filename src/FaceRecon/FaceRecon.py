#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: FaceRecon.py
Description: Aplicacion de deteccion de caras
Author: Alonso Serrano
Created: 180227
Version: 180419
"""

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


###CONFIGURACION

camera=PiCamera() #inicia camara
camera.resolution = (1920,1088) #resolucion
font=cv2.FONT_HERSHEY_SIMPLEX #tipo de fuente

config=configparser.ConfigParser()
config.read(os.path.join(os.path.dirname(__file__), "FaceRecon.ini"))
cfg=config['CONFIG']
fn=cfg['facedetector']
faceDetector = os.path.join(os.path.dirname(__file__), fn)
sKey=cfg['skey']
server=cfg['server']
groupName=cfg['groupname']

waitTime=20 #segundos entre detecciones

CF.Key.set(sKey)
CF.BaseUrl.set('https://'+server+'.api.cognitive.microsoft.com/face/v1.0/')

### COMPROBACION DIRECTORIOS
if not os.path.exists('temp'):
    os.makedirs('temp')
if not os.path.exists('img'):
    os.makedirs('img')
    
### BUCLE PRINCIPAL
try:
    while True:
        now=datetime.now()
        print("\nEjecutando: "+str(now))
        nowStr='_'+str(now.year)+str(now.month)+str(now.day)+'_'+str(now.hour)+str(now.minute)+str(now.second)
        img = fc.captureImage(camera)
        faceList=fc.faceDetect(faceDetector, img, cv2) #detecta
        #finalImage=None
        #files=[]
        personDetected = False
        if len(faceList) == 0:
            print('No se han detectado caras')
            continue # vuelve al principio sin esperar
        else:
            print('Cara/s detectada/s')
            fc.faceCrop(img, cv2, faceList) #si hay caras se recortan
        
        for face in faceList:
            isPerson=FCT.identifyFace(CF, groupName, face)
            if isPerson is True: #no debe ser un objeto
                personDetected = True
                FCT.paintImage(cv2, font, img, face)
                print("Sujeto "+face.name)
        if personDetected is True:
            print("Guardando imagen")
            cv2.imwrite('img/image'+nowStr+'.jpg', img)
            print("Esperando "+str(waitTime)+ " segundos para siguiente ejecución")
            time.sleep(waitTime)
except KeyboardInterrupt: #Ctrl+C finaliza el bucle
    camera.close()
    pass
