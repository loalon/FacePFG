#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: FCTools.py
Description: Metodos para la conexión a Azure
Author: Alonso Serrano
Created: 180228
Version: 180415
"""


def testAzure (service):
    """Realiza un test de funcionamiento, imprime los resultados por pantalla

    Args:
        service: objeto que contiene el servicio de reconocimiento

    """
    img_url = 'https://raw.githubusercontent.com/Microsoft/Cognitive-Face-Windows/master/Data/detection1.jpg'
    result = service.face.detect(img_url)
    print (result)


def getPersonID(service, personGroup, personName):
    """Recupera el personID a partir de un nombre

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        personName: nombre real de la persona

    Returns:
        El ID correspondiente o NOTFOUND si no se encuentra la persona

    """        
    res=service.person.lists(personGroup)
    for person in res:
        if person.get("name") == str(personName):
            return person.get("personId")
    return "NOTFOUND"


def getNameByID(service, personGroup, personID):
    """Recupera el nombre a partir de un personID

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        personID: Id de la persona

    Returns:
        El nombre correspondiente o NOTFOUND si no se encuentra la persona

    """ 
    res=service.person.lists(personGroup)
    for person in res:
        if person.get("personId") == str(personID):
            return person.get("name")
    return "NOTFOUND"


def createPersonGroup(service, personGroup):
    """Crea un grupo de entrenamiento

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
    """ 
    service.person_group.create(personGroup)


def createPerson(service, personGroup, personName):
    """Crea a una persona

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        personName: nombre real de la persona

    """ 
    res=service.person.lists(personGroup)
    for person in res:
        if person.get("name") == str(personName):
            print(personName+" no puede agregarse, ya existe en el sistema")
            return
    res=service.person.create(personGroup, personName)
    print(res)


def addFace(service, personGroup, personName, image):
    """Agrega a una persona

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        personName: nombre real de la persona
        image: imagen con la cara de la persona

    """ 
    id=getPersonID(service, personGroup, personName)
    if id == "NOTFOUND":
        print(personName+" no se encuentra en el sistema, no puede anadirse cara")
    else:
        res=service.person.add_face(image, personGroup, id)
        service.person_group.train(personGroup)
        print(res)

    
def deletePerson(service, personGroup, personName):
    """Borra a una persona

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        personName: nombre real de la persona

    """ 
    personID=getPersonID(service, personGroup, name)
    service.person.delete(personGroup,personID)
    return 0


def deletePersonGroup(service, personGroup):
    """Borra a un grupo

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        personName: nombre real de la persona

    """       
    service.person_group.delete(personGroup)
    return 0  

    
def identifyFace(service, personGroup, face):
    """Borra a una persona

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        face: objeto Face de la persona

    Returns: True si se ha detectado a una persona, False si es un objeto

    """ 
    res=service.face.detect(face.file)
    if not res:
        face.name="Objeto"
        return False #no es una cara
    faceId=[res[0]['faceId']]
    res=service.face.identify(faceId, groupName)
    if len(res[0]['candidates']) == 0:
        face.name="Desconocido"
        return True #desconocido pero persona
    id=res[0]['candidates'][0]['personId']
    face.conf=res[0]['candidates'][0]['confidence']
    face.name=getNameByID(service, groupName, id)
    return True #conocido


def paintImage(cv, font, img, face):
    """Pinta un rectangulo sobre la cara de la persona

    Args:
        cv: objeto que contiene el servicio OpenCV
        font: fuente tipografica
        img: imagen capturada
        face: objeto Face de la persona

    """ 
    x1,y1=face.ulCorner
    x2,y2=face.lrCorner
    cv.rectangle(img,face.ulCorner,face.lrCorner,(0,255,0),0)
    cv.putText(img,face.name, (x1,y1-20), font, 0.8, (0,255,0),2,cv.LINE_AA)

    
def listPersons(service, personGroup):
    """Devuelve un listado de personas de un grupo

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        
    """ 
    res=service.person.lists(personGroup)
    print (res)


def detectPerson(service, personGroup, file):
    """Detecta a una persona en un caraTest

    Args:
        service: objeto que contiene el servicio de reconocimiento
        personGroup. grupo de entrenamiento
        file: archivo del caraTest
        
    """ 
        
    res=service.face.detect(file)
    print("Datos caraTest, archivo: " + file)
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
