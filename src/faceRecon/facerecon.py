import numpy as np
import cv2 as cv2
import time

from picamera.array import PiRGBArray
from picamera import PiCamera
from face.face import Face
from datetime import datetime
import faceCrop.faceCrop as fc
import FCTrain.FCTrain as FCT

import cognitive_face as CF

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



def identifyFace(service, groupName, face):
    res=service.face.detect(face.file)
    #print(res)
    if not res:
        print("No es una cara")
        face.name="Objeto"
        return
    faceId=[res[0]['faceId']]
    res=service.face.identify(faceId, groupName)
    if len(res[0]['candidates']) == 0:
        print("No existe un candidato que concuerde")
        face.name="Desconocido"
        return
    id=res[0]['candidates'][0]['personId']
    face.conf=res[0]['candidates'][0]['confidence']
    face.name=FCT.getNameByID(service, groupName, id)

def paintImage(cv, font, img, face):
    x1,y1=face.ulCorner
    x2,y2=face.lrCorner
    cv2.rectangle(img,face.ulCorner,face.lrCorner,(0,255,0),0)
    cv2.putText(img,face.name, (x1,y1-20), font, 0.8, (0,255,0),2,cv2.LINE_AA)

    
try:
    while True:
        img=fc.captureImage(camera)
        faceList=fc.faceDetect(faceDetector, img, cv2)
        finalImage=None
        files=[]
        if len(faceList)==0:
            print('No face detected!!!')
            continue
        else:
            print('Cara detectada')
            fc.faceCrop(img, cv2, font, faceList)
        
        for face in faceList:
            identifyFace(CF, groupName, face)
            #imprimir
            paintImage(cv2, font, img, face)
            print("Sujeto "+face.name)
        cv2.imwrite('imagen.jpg', img)
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
