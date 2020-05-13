import xmlrpc.client
import datetime
s = xmlrpc.client.ServerProxy('http://localhost:8000')

continuar = True
while continuar:

    operacion = 0
    termX = 0
    termY = 0
    print("---------------------------")
    print ("CALCULADORA BASICA RPC")
    print("---------------------------")
    print ("\t 1. Suma")
    print ("\t 2. Resta")
    print ("\t 3. Multiplicacion")
    print ("\t 4. Division")

    operacion = int(input("Seleccione una operacion\n"))

    if operacion>0 and operacion<6:
        termX = int(input("\tIngrese el primer operando \n"))
        termY = int(input("\tIngrese el segundo operando \n"))

        if operacion == 1:
            print ("\tRESULTADO: ", s.add(termX,termY))
        elif operacion == 2:
            print ("\tRESULTADO: ", s.sub(termX,termY))
        elif operacion == 3:
            print ("\tRESULTADO: ", s.mult(termX,termY))
        elif operacion == 4:
            if termY == 0:
                print ("\tError\n")
            else:
                print ("\tRESULTADO: ", s.div(termX,termY))
    else:
        print("operacion invalida \n")
