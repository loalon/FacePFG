import cognitive_face as CF
import time


#KEY = '6ff97ccedba642f78dc07a821122fc4d'  # Clave de subscripcion.
#CF.Key.set(KEY)

#BASE_URL = 'https://northeurope.api.cognitive.microsoft.com/face/v1.0/'  # Replace with your regional Base URL
#CF.BaseUrl.set(BASE_URL)


def testAzure (service):
	img_url = 'https://raw.githubusercontent.com/Microsoft/Cognitive-Face-Windows/master/Data/detection1.jpg'
	result = service.face.detect(img_url)
	print (result)

def getPersonID(service, personGroup, personName):
	res=service.person.lists(personGroup)
	for person in res:
		if person.get("name") == str(personName):
			return person.get("personId")
	return "NOTFOUND"


def getNameByID(service, personGroup, personID):
	res=service.person.lists(personGroup)
	for person in res:
		if person.get("personId") == str(personID):
			return person.get("name")
	return "NOTFOUND"

# Crear un grupo
def createPersonGroup(service, personGroup):
	service.person_group.create(personGroup)
#res=CF.person_group.create("person1","knowPersons")
#recupera grupo

#res=CF.person_group.get_status("person1")
#print(res)

#Crear persona
def createPerson(service, personGroup, personName):
	res=service.person.lists(personGroup)
	for person in res:
		if person.get("name") == str(personName):
			print(personName+" no puede agregarse, ya existe en el sistema")
			return
	res=service.person.create(personGroup, personName)
	print(res)

		
#res=CF.person.create("person1", "Bill Gates")
#print(res)

def addFace(service, personGroup, personName, image):
	id=getPersonID(service, personGroup, personName)
	if id == "NOTFOUND":
		print(personName+" no se encuentra en el sistema, no puede anadirse cara")
	else:
		res=service.person.add_face(image, personGroup, id)
		service.person_group.train(personGroup)
		print(res)
	
def deletePerson(service, personGroup, name):
	personID=getPersonID(service, personGroup, name)
	service.person.delete(personGroup,personID)
	return 0

def deletePersonGroup(service, personGroup):
	
	return 0
	
def identifyFace(service, personGroup, personID, image):
	#add_face
	#get that face ID
	#res=CF.face.identify(face,"person1")
	#print(res[0]['candidates'])
	return 0
	
def listPersons(service, personGroup):
	res=CF.person.lists(personGroup)
	print (res)
#Listar grupos
#res=CF.person_group.get("person1")
#print(res)


#print (res[0])
#print (getPersonID("Bill Gates"))
#pid = getPersonID("Bill Gates")
#f = open('b0.jpg', "rb")
#body = f.read()
#f.close()
#addFace(CF, "person1", pid, 'b3.jpg')
#res=CF.person_group.train("person1")
#print(res)
"""
res=CF.face.detect('billTest.jpg')
print(res)
face=[res[0]['faceId']]
print(face)
res=CF.face.identify(face,"person1")
print(res[0]['candidates'])
"""

def detectPerson(service, personGroup, file):
	res=service.face.detect(file)
	print("Datos caraTest, archivo: " + file)
	print(res)
	face=[res[0]['faceId']]
	#print(face)
	res=service.face.identify(face,personGroup)
	print("Datos mejor candidato")
	print(res)
	if len(res[0]['candidates']) ==0:
		print("No existe un candidato que concuerde")
		return
	print(res[0]['candidates'])
	id=res[0]['candidates'][0]['personId']
	conf=res[0]['candidates'][0]['confidence']
	name=getNameByID(service, personGroup, id)
	print("Candidato: " + name + " Confianza: " +str(conf))
