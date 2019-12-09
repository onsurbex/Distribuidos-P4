/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author onsur
 */
public class GroupServer extends UnicastRemoteObject implements GroupServerInterface{

    LinkedList<ObjectGroup> groupList;
    int id = 0;
    ReentrantLock lock;
    
    GroupServer() throws RemoteException {
        this.lock = new ReentrantLock();
        this.groupList = new LinkedList<>();
    }
    
    
    @Override
    public int createGroup(String groupAlias, String ownerAlias, String hostname, int nport) throws RemoteException{
        this.lock.lock();
        try {
            if(findGroup(groupAlias) == -1){
                ObjectGroup group = new ObjectGroup(groupAlias,id,ownerAlias,hostname, nport);
                groupList.add(group);
                id++;
                return group.groupID;
            } else {
                return -1;
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public int findGroup(String groupAlias) throws RemoteException{
        this.lock.lock();
        try {
            for (ObjectGroup group : this.groupList) {
                if(group.groupAlias.equals(groupAlias))
                    return group.groupID;
            }
            return -1;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public String findGroup(int groupID) throws RemoteException{
        this.lock.lock();
        try {
            for (ObjectGroup group : groupList) {
                if(group.groupID == groupID)
                    return group.groupAlias;
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean removeGroup(String groupAlias, String ownerAlias) throws RemoteException{
        this.lock.lock();
        try {
            for(int i = 0; i<groupList.size(); i++){
                ObjectGroup ob = groupList.get(i);
                if(ob.groupAlias.equals(groupAlias)){
                    String a = ob.owner.alias;
                    if(true){
                        groupList.remove(i);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
        
    }

    @Override
    public GroupMember addMember(String groupAlias, String alias, String hostname, int nport)throws RemoteException {
        this.lock.lock();
        for(ObjectGroup ob : groupList){
            if(ob.groupAlias.equals(groupAlias)){
                if(ob.isMember(alias) != null){
                    this.lock.unlock();
                    return null;
                } else {
                    try {
                        ob.addMember(alias, hostname, nport);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GroupServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.lock.unlock();
                    return ob.isMember(alias);  
                }
            }
        }
        this.lock.unlock();
        return null;        
    }

    @Override
    public boolean removeMember(String groupAlias, String alias) throws RemoteException{
        this.lock.lock();        
        for(ObjectGroup ob : groupList){
            if(ob.groupAlias.equals(groupAlias)){
                for(int i = 0; i<ob.memberList.size(); i++){
                    if(ob.memberList.get(i).alias.equals(alias)){
                        if(ob.owner.alias.equals(alias)){
                            //cannot remove owner
                            this.lock.unlock();
                            return false;
                        }
                        else {
                            //success
                            ob.memberList.remove(i);
                            this.lock.unlock();
                            return true;
                        }
                    }
                }
                //not found in group
                this.lock.unlock();
                return false;
            }
        }
        return false;
        //group not found        
    }

    @Override
    public GroupMember isMember(String groupAlias, String alias) throws RemoteException{
        this.lock.lock();

        for(ObjectGroup ob: groupList){
            if(ob.groupAlias.equals(groupAlias)){
                //found group
                for(GroupMember gm : ob.memberList){
                    if(gm.alias.equals(alias)){
                        this.lock.unlock();
                        return gm;
                    }
                }
                //member not found in group
                this.lock.unlock();
                return null;
            }
        }
        this.lock.unlock();
        return null;
        //group not found
        
    }

    /*@Override
    public boolean StopMembers(String groupAlias) throws RemoteException{
        this.lock.lock();
        for(ObjectGroup ob: groupList){
            if(ob.groupAlias.equals(groupAlias)){
                this.lock.unlock();
                ob.StopMembers();
                return true;
            }
        }
        this.lock.unlock();
        return false;
    }

    @Override
    public boolean AllowMembers(String groupAlias) throws RemoteException{
        System.out.println("llega");
        for(ObjectGroup ob: groupList){
            if(ob.groupAlias.equals(groupAlias)){
                ob.AllowMembers();
                return true;
            }
        }
        return false;
        
    }
*/

    @Override
    public LinkedList<String> ListMembers(String groupAlias) throws RemoteException {
        this.lock.lock();
        try {
            for(ObjectGroup ob: groupList){
                if(ob.groupAlias.equals(groupAlias)){
                    LinkedList<String> namelist = new LinkedList<>();
                    for(GroupMember gm: ob.memberList){
                        namelist.add(gm.alias);
                    }
                    return namelist;
                }
            }
            return null;
        } finally {
            this.lock.unlock();
        }
        
        
    }

    @Override
    public LinkedList<String> ListGroup() throws RemoteException{
        this.lock.lock();
        LinkedList<String> namelist = new LinkedList<>();
        for(ObjectGroup ob: groupList){
            namelist.add(ob.groupAlias);
        }
        this.lock.unlock();
        return namelist;  
    }
    
    @Override
    public boolean sendGroupMessage(GroupMember gm, byte[] msg){
        lock.lock();
        try {
            int group_id = gm.groupID;
            for (ObjectGroup og : this.groupList){
                if (og.groupID == group_id){
                    return og.sendGroupMessage(gm, msg);
                }
            }
            System.out.println("ERROR: group_id not found");
            return false;
        }
        finally {
            lock.unlock();
        }
        
    }
    
    public static void main(String[] args){
        System.setProperty("java.security.policy", "C:\\Users\\verde\\Documents\\NetBeansProjects\\Distribuidos-P4\\CentralizedGroups\\politicaDelServidor");
        if(System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        
        try {
            LocateRegistry.createRegistry(1099);  
            GroupServer groupServer = new GroupServer();
            Naming.bind("GroupServer", groupServer);
        } catch (RemoteException | AlreadyBoundException | MalformedURLException ex) {
            System.err.println("Server exception: " + ex.toString());
            ex.printStackTrace();
        }
    }
}
