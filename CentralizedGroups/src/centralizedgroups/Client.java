/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author onsur & green
 */
public class Client extends UnicastRemoteObject implements ClientInterface {
    
    String clientAlias;
    String hostname;
    
    public Client() throws RemoteException{
        super();
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Scanner s = new Scanner(System.in);
        
        System.out.println("Introduzca el alias: ");
        this.clientAlias = s.nextLine();
    }
    
    public static void main(String[] args) throws RemoteException{
        System.setProperty("java.security.policy", "C:\\Users\\verde\\Documents\\NetBeansProjects\\S.-Distribuidos-P3\\CentralizedGroups\\ClientPolicy");
        System.setSecurityManager(new SecurityManager());
        
        GroupServerInterface server = null;
        
        if(args.length == 0){
            System.out.println("Falta el hostname como argumanto del programa");
            System.exit(0);
        }
        String serverHostname = args[0];
        try{
            Registry registry = LocateRegistry.getRegistry(serverHostname, 1099);
            server = (GroupServerInterface) registry.lookup("GroupServer");
        } catch(Exception e){
            System.err.println("Exception: " + e.toString());
        }    
        
        
        Client client = new Client();
        String memberAlias = null;
        String groupAlias = null;
        int res; 
        LinkedList<String> namelist;
        int option = 0;
        Scanner s = new Scanner(System.in);
        System.out.println("Bienvenido al cliente de Grupos");
        System.out.println("Estas son las opciones que dispone");
        System.out.println("Eliga una: ");
        while(option != 9){
            System.out.println("----------------");
            System.out.println(" 1. Crear grupo");
            System.out.println(" 2. Eliminar grupo");
            System.out.println(" 3. Añadir miembro al grupo");
            System.out.println(" 4. Eliminar miembro del grupo");
            System.out.println(" 5. Bloquear altas/bajas");
            System.out.println(" 6. Desbloquear altas/bajas");
            System.out.println(" 7. Mostrar miembros del grupo");
            System.out.println(" 8. Mostrar grupos actuales");
            System.out.println(" 9. Salir");
            System.out.print("Opcion [1-9]: ");
            try {
                option = s.nextInt();
                s.nextLine();
            } catch (Exception e){
                System.err.println(e);
                option = 9;
            }
            System.out.println();
            if(option <= 0 || option > 9){
                System.out.println("Opcion no valida: Numero no recogido en las opciones");
            } else {
                String alias;
                try {
                switch(option){
                    
                    case (1):
                        
                        System.out.println("Introduzca un alias para el grupo: ");
                        groupAlias = s.nextLine();
                        res = server.createGroup(groupAlias, client.clientAlias, client.hostname);
                        if(res == -1){
                            System.out.println("ERROR al crear el grupo");
                        } else {
                            System.out.println("Grupo creado CORRECTAMENTE");
                        }
                        break;
                    case (2):
                        System.out.println("Nombra el grupo que quieras eliminar");
                        groupAlias = s.nextLine();
                        if(!server.removeGroup(groupAlias, client.clientAlias))
                            System.out.println("ERROR al eliminar grupo");
                        else
                            System.out.println("Grupo eliminado");
                        break;
                    case (3):
                        System.out.println("Introduzca alias del grupo: ");
                        groupAlias = s.nextLine();
                        
                        System.out.println("Introduzca alias para el miembro del grupo: ");
                        memberAlias = s.nextLine();
                        
                        if(server.addMember(groupAlias, memberAlias, client.hostname) != null)
                            System.out.println("Mimbro creado correctamente");
                        else
                            System.out.println("Error al crear el miembro");
                        break;
                    case (4):
                        System.out.println("Nombra el grupo del que quieras eliminar");
                        groupAlias = s.nextLine();
                        System.out.println("Nombra el usuario al que quieras expulsar. No puedes eliminar al owner");
                        alias = s.nextLine();
                        if(!server.removeMember(groupAlias, alias))
                            System.out.println("ERROR: usuario no expulsado");
                        else
                            System.out.println("El usuario "+alias+" ha sido expulsado de "+groupAlias);
                        break;
                    case (5):
                        System.out.println("Introduzca alias del grupo: ");
                        groupAlias = s.nextLine();
                        if(server.StopMembers(groupAlias))
                            System.out.println("Altas y bajas bloqueadas CORRECTAMENTE");
                        else
                            System.out.println("ERROR al bloquear las altas/bajas");
                        break;
                    case (6):
                        System.out.println("Nombra el grupo a desbloquear");
                        groupAlias = s.nextLine();
                        
                        if(!server.AllowMembers(groupAlias))
                            System.out.println("ERROR: El grupo no existe");
                        else
                            System.out.println(groupAlias + " ha sido desbloqueado");
                        break;
                    case (7):
                        System.out.println("Introduzca alias del grupo a mostrar: ");
                        groupAlias = s.nextLine();
                        if(server.findGroup(groupAlias) != -1){
                            namelist = server.ListMembers(groupAlias);
                            for (int i = 0; i<namelist.size(); i++) {
                                System.out.println((i+1)+": "+namelist.get(i));
                            }
                        } else {
                            System.out.println("Ese grupo no existe!");
                        }
                        break;
                    case (8):
                        System.out.println("Lista de grupos actuales del servidor:");
                        namelist = server.ListGroup();
                        for(int i = 0; i<namelist.size(); i++){
                            System.out.println((i+1)+": "+namelist.get(i));
                        }
                        break;
                    case (9):
                        System.out.println("Cerrando cliente");
                        System.exit(0);
                        break;
                }
                } catch (RemoteException e) {
                    System.err.println("Excepcion Remota: " + e.toString());
                }
            }
        }
    }
}
