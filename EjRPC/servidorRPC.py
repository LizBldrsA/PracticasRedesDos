from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler


class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)
with SimpleXMLRPCServer(('localhost', 8000),logRequests=True, requestHandler=RequestHandler) as server:
    server.register_introspection_functions()

    class MyFuncs:
        def add(self, x, y):
            return x + y

        def sub(self, x, y):
            return x - y

        def mult(self, x, y):
            return x * y

        def div(self, x, y):
            return x / y

        def show_type(self, arg):
            return (str(arg), str(type(arg)), arg)

    server.register_instance(MyFuncs())

    server.serve_forever()