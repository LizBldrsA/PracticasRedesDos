import logging
import random
import threading
import time

logging.basicConfig(level=logging.DEBUG,format='(%(threadName)-10s) %(message)s',)

class TallerDeCostura(object):
    CAPACIDAD_CANASTA_MANGAS = 10
    CAPACIDAD_CANASTA_CUERPOS = 5
    CHAMARRAS_MAXIMAS = 100;

    def __init__(self):
        self.canastaDeMangas = 0
        self.canastaDeCuerpos = 0
        self.canastaDeChamarrasCreadas = 0;
        self.condicionMangas = threading.Condition()
        self.condicionCuerpo = threading.Condition()

    def agregarMangaACanasta (self):
        self.canastaDeMangas = self.canastaDeMangas+1

    def usarManagas (self):
        self.canastaDeMangas = self.canastaDeMangas-2

    def usarCuerpo (self):
        self.canastaDeCuerpos = self.canastaDeCuerpos-1

    def agregarCuerpoACanasta (self):
        self.canastaDeCuerpos = self.canastaDeCuerpos+1

    def obtenerCanastaDeMangas(self):
        return self.canastaDeMangas

    def obtenerCanastaDeCuerpos(self):
        return self.canastaDeCuerpos

    def agregarChamarraCreadaACanasta(self):
        self.canastaDeChamarrasCreadas = self.canastaDeChamarrasCreadas+1

    def obtenerChamarrasCreadas(self):
        return self.canastaDeChamarrasCreadas

    def obtenerCondicionMangas(self):
        return self.condicionMangas

    def obtenerCondicionCuerpo(self):
        return self.condicionCuerpo

class CostureroDeMangas(object):

    def crearManga(self,taller):
        logging.debug("El costurero quiere crear una manga")
        condicionMangas = taller.obtenerCondicionMangas()
        with condicionMangas:
            condicionMangas.acquire()
            try:
                if (taller.obtenerCanastaDeMangas() < taller.CAPACIDAD_CANASTA_MANGAS):
                    time.sleep(random.randint(1, 5))
                    taller.agregarMangaACanasta()
                    logging.debug("El costurero creo una manga y la agrego a a canasta, la canasta tiene " + str(
                        taller.obtenerCanastaDeMangas()) + " mangas")
            finally:
                condicionMangas.release()

class CostureroDeCuerpo(object):

    def crearCuerpo(self,taller):
        logging.debug("El costurero quiere crear un cuerpo")
        condicionCuerpo = taller.obtenerCondicionCuerpo()
        with condicionCuerpo:
            condicionCuerpo.acquire()
            try:
                if (taller.obtenerCanastaDeCuerpos() < taller.CAPACIDAD_CANASTA_CUERPOS):
                    time.sleep(random.randint(1, 5))
                    taller.agregarCuerpoACanasta()
                    logging.debug("El costurero creo un cuerpo y la agrego a a canasta, la canasta tiene " + str(
                        taller.obtenerCanastaDeCuerpos()) + " cuerpos")
            finally:
                condicionCuerpo.release()

class Ensamblador(object):

    def ensamblar(self,taller):
        condicionMangas = taller.obtenerCondicionMangas()
        logging.debug("El ensamblador quiere ensamblar una chamarra")
        with condicionMangas:
            condicionMangas.acquire()
            try:
                if(taller.obtenerCanastaDeMangas()>2):
                    condicionCuerpos = taller.obtenerCondicionCuerpo()
                    with condicionCuerpos:
                        condicionCuerpos.acquire()
                        try:
                            if (taller.obtenerCanastaDeCuerpos() > 1):
                                taller.usarManagas()
                                taller.usarCuerpo()
                                time.sleep(random.randint(1, 5))
                                taller.agregarChamarraCreadaACanasta()
                                logging.debug("El ensamblador creo una chamarra, en total son "+str(taller.obtenerChamarrasCreadas())+" chamarras , hay "+str(taller.obtenerCanastaDeCuerpos())+" cuerpos y "+str(taller.obtenerCanastaDeMangas())+" mangas ")
                        finally:
                            condicionCuerpos.release()
            finally:
                condicionMangas.release()


TALLER_DE_COSTURA = TallerDeCostura()

def workerCosturerosDeMangas(taller):
    costureroDeMangas = CostureroDeMangas()
    while (taller.obtenerChamarrasCreadas() < taller.CHAMARRAS_MAXIMAS):
        costureroDeMangas.crearManga(taller)
        costureroDeMangas.crearManga(taller)

def workerCosturerosDeCuerpos(taller):
    costureroDeCuerpos = CostureroDeCuerpo()
    while (taller.obtenerChamarrasCreadas()<taller.CHAMARRAS_MAXIMAS):
        costureroDeCuerpos.crearCuerpo(taller)

def workerEnsambladores(taller):
    ensamblador = Ensamblador()
    while (taller.obtenerChamarrasCreadas() < taller.CHAMARRAS_MAXIMAS):
        ensamblador.ensamblar(taller)

hiloCostureroDeMangas = threading.Thread(target=workerCosturerosDeMangas, args=(TALLER_DE_COSTURA,))
hiloCostureroDeMangas.start()

hiloCostureroDeCuerpos = threading.Thread(target=workerCosturerosDeCuerpos, args=(TALLER_DE_COSTURA,))
hiloCostureroDeCuerpos.start()

hiloEnsamblador = threading.Thread(target=workerEnsambladores, args=(TALLER_DE_COSTURA,))
hiloEnsamblador.start()