import time

from picamera.array import PiRGBArray
from picamera import PiCamera
from .face import Face
from datetime import datetime

def captureImage(camera):
    rawCapture= PiRGBArray(camera)
    time.sleep(0.1)
    camera.capture(rawCapture, format="bgr")

    img=rawCapture.array
    return img #un array 3D RGB

def faceDetect(faceDetector, image, cv2):
    faceCascade = cv2.CascadeClassifier(faceDetector)
    gray=cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(gray, 1.1, 4)
    faceList=[]
    for (x,y,w,h) in faces:
        f=Face((x,y),(x+w,y+h))
        faceList.append(f)
    return faceList

#def faceGenerator(faces):
    

def faceCrop(image, cv2, font, faceList):
    """!@brief Detecta caras y devuelve sus recortes

    Devuelve...
    @param image La imagen capturada
    @return lista de caras

    """
    fileCount=1
    #files=[]

    for face in faceList:
        x1,y1=face.ulCorner
        x2,y2=face.lrCorner
        #cv2.rectangle(image,face.ulCorner,face.lrCorner,(0,255,0),0)
        #cv2.putText(image,'Admin!!', (x1,y1-20), font, 0.8, (0,255,0),2,cv2.LINE_AA)

        #roi_gray = gray[y:y+h, x:x+w]
        roi_color = image[y1:y2, x1:x2]
        cFilename='temp/cropped'+str(fileCount)+'.jpg'
        cv2.imwrite(cFilename, roi_color)
        face.file=cFilename
        #files.append(cFilename)
        #cv2.imwrite('cropped'+str(fileCount)+'.jpg', roi_color)
        #cv2.imwrite('imagen.jpg', image)
        fileCount+=1
    #return files
