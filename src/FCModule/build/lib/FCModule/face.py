#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
File: face.py
Description: Clase Face
Author: Alonso Serrano
Created: 180228
Version: 180310
"""

class Face():
    """ Clase para contener datos de los objetos Face

    Atributes:
        ulCorner: esquina superior izquierda (tuple)
        lrcorner: esquina inferior derecha (tuple)
        name: nombre de la persona
        file: archivo que contiene el recorte
    """

    def __init__(self, ulCorner, lrCorner):
        self.ulCorner = ulCorner
        self.lrCorner = lrCorner
        self.name=""
        self.file=""
