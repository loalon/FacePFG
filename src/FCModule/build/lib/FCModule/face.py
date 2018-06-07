#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: face.py
Description: Clase Face
Author: Alonso Serrano
Created: 180228
Version: 180601
"""

class Face():
    """ Clase para contener datos de los objetos Face

    Atributes:
        ulCorner: esquina superior izquierda (tuple)
        lrcorner: esquina inferior derecha (tuple)
        name: nombre del candidato mas probable
        file: archivo que contiene el recorte
        confidence: probabilidad del candidato mas probable
        candidates: lista de todos los candidatos posibles
        personId: identificador de Azure del mejor candidato
    """

    def __init__(self, ulCorner, lrCorner):
        self.ulCorner = ulCorner
        self.lrCorner = lrCorner
        self.name=""
        self.file=""
        self.confidence=0
        self.candidates=[]
        self.personID=""
