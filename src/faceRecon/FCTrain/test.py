import FCTrain as FCT
import cognitive_face as CF
import time

KEY = '6ff97ccedba642f78dc07a821122fc4d'  # Clave de subscripción.
CF.Key.set(KEY)

BASE_URL = 'https://northeurope.api.cognitive.microsoft.com/face/v1.0/'  # Replace with your regional Base URL
CF.BaseUrl.set(BASE_URL)

subFolder = "test\\"
billList=['bill0.jpg','bill1.jpg','bill2.jpg','bill3.jpg','bill4.jpg','bill5.jpg',
'bill6.jpg','bill7.jpg','bill8.jpg','bill9.jpg']
benList=['ben0.jpg','ben1.jpg','ben2.jpg','ben3.jpg','ben4.jpg','ben5.jpg',
'ben6.jpg','ben7.jpg','ben8.jpg','ben9.jpg']
#Creacion de Bill Gates
"""
FCT.createPerson(CF,"person1", "Bill Gates")
for file in fileList:
	addFace(CF, "person1", "Ben Linus", file)
	detectPerson(CF, "person1",subFolder+'billTest.jpg')
"""
#Creacion de Ben Linus
"""
FCT.createPerson(CF,"person1", "Ben Linus")
for file in fileList:
	addFace(CF, "person1", "Ben Linus", file)
	detectPerson(CF, "person1",subFolder+'benTest.jpg')
"""

#Bill vs. Ben
#Primero todas las imagenes de Bill
for file in billList:
	FCT.detectPerson(CF, "person1",subFolder+file)
	time.sleep(20) #necesario para saltar el limite de Azure
#Todas las imagenes de Ben
for file in benList:
	FCT.detectPerson(CF, "person1",subFolder+file)
	time.sleep(20)

#Imitador de Bill
FCT.detectPerson(CF, "person1",subFolder+'billImpersonator.jpg')