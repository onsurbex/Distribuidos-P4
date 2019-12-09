/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import static centralizedgroups.Client.server;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author onsur
 */
class SendingMessage extends Thread {

    
    GroupMessage gm; // Mensaje que se envía
    ObjectGroup og;
    GroupMember r; // Receptor
    
    public SendingMessage(GroupMessage gm, ObjectGroup og, GroupMember r) {
        super();
        this.gm = gm;
        this.og = og;
        this.r = r;
        this.start();
    }
    
    @Override
    public void run(){
        Random rand = new Random();
        System.out.println("AQUI ESTOY uoUUUUUUUUU");
        int retraso = (rand.nextInt(1)+1)*1000;
        try {
            System.setProperty("java.security.policy", "C:\\Users\\verde\\Documents\\NetBeansProjects\\Distribuidos-P4\\CentralizedGroups\\politicaDelServidor");
            if(System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            //encontrar cliente
            ClientInterface client = null;
            try{
                Registry registry = LocateRegistry.getRegistry(r.hostname, r.nport);
                client = (ClientInterface) registry.lookup(this.r.alias);
            } catch(Exception e){
                System.err.println("Exception: " + e.toString());
            }

            try{
                Thread.sleep(retraso);
            } catch (InterruptedException ex) {
                Logger.getLogger(SendingMessage.class.getName()).log(Level.SEVERE, null, ex);
            }
            //enviarselo con DepositMsg

            client.DepositMessage(gm);
            System.out.println("Mensaje: " + this.gm.msg.toString());

            og.EndSending();
            System.out.println("burno pues el mensaje ya lo he enviao");
        } catch (Exception ex){
            System.out.println("Error: " + ex);
        }
        
    }
    
}
