import logging
import threading
import time

logging.basicConfig(level=logging.DEBUG,format='(%(threadName)-10s) %(message)s',)

class Libro(object):
    def __init__(self):
        self.valorLibro = 0
        self.lock = threading.Lock()

    def escribirLibro(self):
        self.valorLibro = self.valorLibro+1;

    def leerLibro(self):
        return  self.valorLibro

    def traerCandadoDelLibro(self):
        return self.lock
class Lector(object):
    def __init__(self,numero):
        self.numero = numero;

    def leerLibro(self,libro):
        logging.debug('El lector ' + str(self.numero) + " intento acceder al libro")
        libro.traerCandadoDelLibro().acquire()
        try:
            logging.debug("El lector " + str(self.numero) + " accedio al libro, y leyo " + str(libro.leerLibro()))
            time.sleep(3)
        finally:
            logging.debug("El lector " + str(self.numero) + " dejo de usar el libro")
            libro.traerCandadoDelLibro().release()

class Escritor(object):
    def __init__(self,numero):
        self.numero = numero

    def escribirLibro(self,libro):
        logging.debug('El escritor '+str(self.numero)+" intento acceder al libro")
        libro.traerCandadoDelLibro().acquire()
        try:
            libro.escribirLibro()
            logging.debug("El escritor " + str(self.numero) + " accedio al libro, y ahora vale " + str(libro.leerLibro()))
            time.sleep(3)
        finally:
            logging.debug("El escritor " + str(self.numero) + " dejo de usar el libro")
            libro.traerCandadoDelLibro().release()



def workerLectores(lector,libro):
    lector.leerLibro(libro)

def workerEscritores(escritor,libro):
    escritor.escribirLibro(libro)


LIBRO = Libro()

for i in range(2):
    t = threading.Thread(target=workerLectores, args=(Lector(i), LIBRO,))
    t.start()

for i in range(2):
    t = threading.Thread(target=workerEscritores, args=(Escritor(i), LIBRO,))
    t.start()
