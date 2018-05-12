#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: faceCrop.py
Description: Metodos de deteccion y recorte de caras
Author: Alonso Serrano
Created: 180228
Version: 180512
"""

import time

from picamera.array import PiRGBArray
from picamera import PiCamera
from .face import Face


def captureImage(camera):
    """Captura una imagen

    Args:
        camera: objeto que contiene la PiCamera

    Returns:
        img: array 3D RGB que contiene la imagen capturada

    """
    rawCapture = PiRGBArray(camera)
    time.sleep(0.1)
    camera.capture(rawCapture, format="bgr")

    img = rawCapture.array
    return img 


def faceDetect(faceDetector, image, cv2):
    """Detecta caras, genera objetos Face con las
    coordenadas de cada cara y devuelve un lista
    con los objetos Face

    Args:
        image La imagen capturada
        cv2: objeto OpenCV
        faceDetector: filtro Haar

    Returns:
        faceList: lista de objetos Face

    """
    faceCascade = cv2.CascadeClassifier(faceDetector)
    gray=cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(gray, 1.1, 4)
    faceList=[]
    for (x,y,w,h) in faces:
        f=Face((x,y),(x+w,y+h))
        faceList.append(f)
    return faceList


def faceCrop(image, cv2, faceList):
    """Genera archivos con
    los recortes y modifica la lista de caras
    indicando los archivos asociados

    Args:
        image La imagen capturada
        cv2: objeto OpenCV
        faceList: lista de objetos Face

    """
    fileCount=1

    for face in faceList:
        x1,y1 = face.ulCorner
        x2,y2 = face.lrCorner
        roi_color = image[y1:y2, x1:x2]
        cFilename = 'temp/cropped'+str(fileCount)+'.jpg'
        cv2.imwrite(cFilename, roi_color)
        face.file = cFilename
        fileCount+=1

