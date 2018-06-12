#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: faceCrop.py
Description: Metodos de deteccion y recorte de caras
Author: Alonso Serrano
Created: 180228
Version: 180512
"""

## @package faceCrop
# Contiene las funciones de gestión de las imágenes 
# relacionadas con el recorte de caras.

import time

from picamera.array import PiRGBArray
from picamera import PiCamera
from .face import Face

## Captura una imagen desde la camara de Raspberry Pi.
# 
# @param camera Objeto que contiene la cámara de la Raspberry Pi
# @returns img Array 3D RGB que contiene la imagen capturada
def captureImage(camera):
    rawCapture = PiRGBArray(camera)
    time.sleep(0.1)
    camera.capture(rawCapture, format="bgr")
    img = rawCapture.array
    return img 

## Detecta caras, genera objetos Face con las
# coordenadas de cada cara y devuelve un lista
# con los objetos Face
# 
# @param faceDetector XML que contiene la información sobre el filtro de Haar.
# @param image objeto PiRGBArray que contiene la imagen.
# @param cv2 objeto con las funciones de OpenCV.

# @returns faceList un array de objetos Face detectados en la imagen
def faceDetect(faceDetector, image, cv2):
    faceCascade = cv2.CascadeClassifier(faceDetector)
    gray=cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(gray, 1.1, 4)
    faceList=[]
    
    for (x,y,w,h) in faces:
        f=Face((x,y),(x+w,y+h))
        faceList.append(f)
    return faceList

## Genera archivos conlos recortes y modifica la lista de caras
# indicando los archivos asociados
# 
# @param image objeto PiRGBArray que contiene la imagen.
# @param cv2 objeto con las funciones de OpenCV.
# @param faceList un array de objetos Face detectados en la imagen.
def faceCrop(image, cv2, faceList):
    fileCount=1

    for face in faceList:
        x1,y1 = face.ulCorner
        x2,y2 = face.lrCorner
        roi_color = image[y1:y2, x1:x2]
        cFilename = 'temp/cropped'+str(fileCount)+'.jpg'
        cv2.imwrite(cFilename, roi_color)
        face.file = cFilename
        fileCount+=1

