import numpy as np

import time
from datetime import datetime
from picamera.array import PiRGBArray
from picamera import PiCamera

import cognitive_face as CF

import cv2 as cv2
import faceRecon.faceCrop as fc
import faceRecon.FCTools as FCT
from faceRecon.face import Face



KEY = '6ff97ccedba642f78dc07a821122fc4d'  # Clave de subscripción.
CF.Key.set(KEY)

BASE_URL = 'https://northeurope.api.cognitive.microsoft.com/face/v1.0/'  # Replace with your regional Base URL
CF.BaseUrl.set(BASE_URL)


faceDetector ='utils/haarcascade_frontalface_alt2.xml'
camera=PiCamera()
camera.resolution = (1920,1088)
font=cv2.FONT_HERSHEY_SIMPLEX
groupName="conocidos"
waitTime=20

    
try:
    while True:
        now=datetime.now()
        nowStr='_'+str(now.year)+str(now.month)+str(now.day)+'_'+str(now.hour)+str(now.minute)+str(now.second)
        img=fc.captureImage(camera)
        faceList=fc.faceDetect(faceDetector, img, cv2)
        finalImage=None
        files=[]
        personDetected=False
        if len(faceList)==0:
            print('No face detected!!!')
            continue
        else:
            print('Cara detectada')
            fc.faceCrop(img, cv2, font, faceList)
        
        for face in faceList:
            isPerson=FCT.identifyFace(CF, groupName, face)
            #imprimir
            if isPerson is True:
                personDetected = True
                FCT.paintImage(cv2, font, img, face)
                print("Sujeto "+face.name)
        if personDetected is True:
            cv2.imwrite('img/image'+nowStr+'.jpg', img)
            time.sleep(waitTime)
except KeyboardInterrupt:
    camera.close()
    pass


#camera.start_preview()
"""
    #!@brief Detecta caras y devuelve sus recortes

    #Devuelve...
    #@param image La imagen capturada
    #@return lista de caras

"""
