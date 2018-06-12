#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: face.py
Description: Clase Face
Author: Alonso Serrano
Created: 180228
Version: 180601
"""
## @package Face
#  Documentation for this module.
#
#  More details.


## Clase Face
#
# Clase para contener datos de los objetos Face
class Face():

    ##Constructor
    def __init__(self, ulCorner, lrCorner):
        self.ulCorner = ulCorner
        self.lrCorner = lrCorner
        self.name=""
        self.file=""
        self.confidence=0
        self.candidates=[]
        self.personID=""

    ## @var self.ulCorner
    # esquina superior izquierda (tuple)
    ## @var lrCorner
    # esquina inferior derecha (tuple)
    ## @var name
    # nombre del candidato mas probable
    ## @var file
    # archivo que contiene el recorte
    ## @var confidence
    # probabilidad del candidato mas probable
    ## @var candidates
    # lista de todos los candidatos posibles
    ## @var personID
    # identificador de Azure del mejor candidato
    