from setuptools import setup

setup(name='FCModule',
      version='1.0',
      description='Modulo de reconocimiento facial',
      author='Alonso Serrano',
      author_email='aserrano543@alumno.uned.es',
      packages=['FCModule'],
      install_requires=[
          'numpy>=1.13.1',
          'opencv-python>=3.4.0.12',
          'cognitive-face>=1.3.1'
      ]
)