#!/usr/bin/python
# -*- coding: utf-8 -*-

# ce script crée un fichier scene.txt contenant des objets aléatoires

from random import uniform, randint

with open('scene.txt', 'w') as scene:

    # créer des sphères
    for i in range(1,50):
        scene.write('objet %d\n'%i)
        scene.write('	centre %.2f %.2f %.2f\n'%(0.25*randint(-15,+15), 0.25*randint(-15, +15), 0.25*randint(0,12)+10))
        scene.write('	rayon %.2f\n'%uniform(0.125, 1.25))
        scene.write('	Kd %.2f %.2f %.2f\n'%(uniform(0,1), uniform(0,1), uniform(0,1)))
        scene.write('	Ks %.2f %.2f %.2f\n'%(uniform(0,1), uniform(0,1), uniform(0,1)))
        scene.write('	Ns %d\n'%(15*randint(5, 25)))
        scene.write('\n')

    # créer des lampes
    for i in range(1,20):
        scene.write('lampe %d\n'%i)
        scene.write('	position %.2f %.2f %.2f\n'%(uniform(-12,+12), uniform(-12, +12), uniform(5,20)))
        scene.write('	couleur %.2f %.2f %.2f\n'%(uniform(0,1), uniform(0,1), uniform(0,1)))
        scene.write('\n')
