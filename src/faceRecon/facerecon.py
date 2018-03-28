import numpy as np
import cv2 as cv2
import time

from picamera.array import PiRGBArray
from picamera import PiCamera
from face.face import Face
from datetime import datetime
import faceCrop.faceCrop as fc

faceDetector ='utils/haarcascade_frontalface_alt2.xml'
camera=PiCamera()
camera.resolution = (1920,1088)
font=cv2.FONT_HERSHEY_SIMPLEX

try:
    while True:
        img=fc.captureImage(camera)
        faceList=fc.faceDetect(faceDetector, img, cv2)
        finalImage=None
        if len(faceList)==0:
            print('No face detected!!!')
        else:
            print('More than one face!!!')
            finalImage=fc.faceCrop(img, cv2, font, faceList)
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
