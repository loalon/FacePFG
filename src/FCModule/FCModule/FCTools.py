#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: FCTools.py
Description: Metodos para la conexión a Azure
Author: Alonso Serrano
Created: 180228
Version: 180512
"""

## @package FCTools
# Contiene funciones para la puesta en marcha, el entrenamiento del sistema y 
# funciones necesarias para el reconocimiento facial.

##Realiza un test de funcionamiento, imprime los resultados por pantalla
#
# @param service objeto que contiene el servicio de reconocimiento

def testAzure (service):

    img_url = 'https://raw.githubusercontent.com/Microsoft/Cognitive-Face-Windows/master/Data/detection1.jpg'
    result = service.face.detect(img_url)
    print (result)

##Realiza un test de funcionamiento, imprime los resultados por pantalla
#
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
# @param personName Nombre real de la persona
# @returns ID correspondiente o NOTFOUND si no se encuentra la persona
def getPersonID(service, personGroup, personName):
    res=service.person.lists(personGroup)
    for person in res:
        if person.get("name") == str(personName):
            return person.get("personId")
    return "NOTFOUND"

##Recupera el nombre a partir de un personID
#
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
# @param personID Id de la persona
# @returns El nombre correspondiente o NOTFOUND si no se encuentra la persona
def getNameByID(service, personGroup, personID):

    res=service.person.lists(personGroup)
    for person in res:
        if person.get("personId") == str(personID):
            return person.get("name")
    return "NOTFOUND"

##Crea un grupo de entrenamiento
#
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
def createPersonGroup(service, personGroup):
    service.person_group.create(personGroup)

##Crea a una persona
#
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
# @param personName Nombre real de la persona
def createPerson(service, personGroup, personName):
    res=service.person.lists(personGroup)
    for person in res:
        if person.get("name") == str(personName):
            print(personName+" no puede agregarse, ya existe en el sistema")
            return
    res=service.person.create(personGroup, personName)
    print(res)

##Agrega a una persona
#
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
# @param personName Nombre real de la persona
# @param image Imagen con la cara de la persona
def addFace(service, personGroup, personName, image):

    id=getPersonID(service, personGroup, personName)
    if id == "NOTFOUND":
        print(personName+" no se encuentra en el sistema, no puede anadirse cara")
    else:
        res=service.person.add_face(image, personGroup, id)
        service.person_group.train(personGroup)
        print(res)

##Borra a una persona
#
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
# @param personName Nombre real de la persona
def deletePerson(service, personGroup, personName):
    personID=getPersonID(service, personGroup, name)
    service.person.delete(personGroup,personID)
    return 0

##Borra a un grupo
#
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
def deletePersonGroup(service, personGroup):
    
    service.person_group.delete(personGroup)
    return 0  

## Esta función conecta con Azure, identifica la cara y pasa el nombre 
# identificado al objeto Face correspondiente. No devuelve ningún tipo de dato,
# solo modifica sobre la marcha cada objeto Face que esté contenido en el 
# array de caras.
# 
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
# @param face objeto Face de la persona
# @returns True si se ha detectado a una persona, False en otro caso
def identifyFace(service, personGroup, face):

    res=service.face.detect(face.file)
    if not res:
        face.name="Objeto"
        return False #no es una cara
    faceId=[res[0]['faceId']]
    res=service.face.identify(faceId, personGroup)
    if len(res[0]['candidates']) == 0:
        face.name="Desconocido"
        return True #desconocido pero persona
    id=res[0]['candidates'][0]['personId']
    face.confidence=res[0]['candidates'][0]['confidence']
    face.name=getNameByID(service, personGroup, id)
    face.personID=id
    if len(res[0]['candidates']) > 1:
        for cand in res[0]['candidates']:
            name=getNameByID(service, personGroup, id)
            face.candidates.append([id, name, cand['confidence']])
    else:
        face.candidates.append([id, face.name, face.confidence])
    return True #conocido

## Esta función toma las coordenadas de un objeto Face y dibuja un rectángulo 
# que enmarca la cara, además de escribir el nombre correspondiente a la cara.
# 
# @param cv Objeto que contiene el servicio OpenCV
# @param font fuente tipografica
# @param img imagen capturada
# @param face objeto Face de la persona
def paintImage(cv, font, img, face):
    x1,y1=face.ulCorner
    x2,y2=face.lrCorner
    cv.rectangle(img,face.ulCorner,face.lrCorner,(0,255,0),0)
    cv.putText(img,face.name, (x1,y1-20), font, 0.8, (0,255,0),2,cv.LINE_AA)

## Pinta un rectangulo sobre todas las caras y su nombre
# 
# @param cv Objeto que contiene el servicio OpenCV
# @param font fuente tipografica
# @param img imagen capturada
# @param faceList lista de objetos Face
def paintAllFaces(cv, font, img, faceList):
    for face in faceList:
        FCT.paintImage(cv2, font, img, face)
    
    return img

## Devuelve un listado de personas de un grupo
# 
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
def listPersons(service, personGroup):
    res=service.person.lists(personGroup)
    print (res)

## Detecta a una persona en un archivo
# 
# @param service Objeto que contiene el servicio de reconocimiento
# @param personGroup Grupo de entrenamiento
# @param file archivo con imagen de persona
def detectPerson(service, personGroup, file):
    res=service.face.detect(file)
    print("Datos imagen, archivo: " + file)
    print(res)
    face=[res[0]['faceId']]
    res=service.face.identify(face,personGroup)
    print("Datos mejor candidato: ")
    print(res)
    if len(res[0]['candidates']) ==0:
        print("No existe un candidato que concuerde.")
        return
    print(res[0]['candidates'])
    id=res[0]['candidates'][0]['personId']
    conf=res[0]['candidates'][0]['confidence']
    name=getNameByID(service, personGroup, id)
    print("Candidato: " + name + " Confianza: " +str(conf))
