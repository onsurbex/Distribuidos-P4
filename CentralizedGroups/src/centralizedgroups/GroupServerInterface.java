/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author onsur
 */
public interface GroupServerInterface extends Remote{
    
    /**
     * 
     * @param groupAlias Group alias identifier
     * @param ownerAlias Group owner identifier
     * @param ownerHostname Hostname of the owner
     * @return  n>=0 - Group identifier integer greater or equal than 0
     *            -1 - Error  
     */
    public int createGroup(String groupAlias,String ownerAlias, String ownerHostname, int nport) throws RemoteException;
    
    public int findGroup(String groupAlias) throws RemoteException;
    
    public String findGroup(int groupID) throws RemoteException;
    
    public boolean removeGroup(String groupAlias, String ownerAlias) throws RemoteException;
    
    public GroupMember addMember(String groupAlias, String alias, String hostname, int nport) throws RemoteException;
    
    public boolean removeMember(String groupAlias, String alias) throws RemoteException;
    
    public GroupMember isMember(String groupAlias, String alias) throws RemoteException;
    
    public boolean sendGroupMessage(GroupMember gm, byte[] msg) throws RemoteException;
    //public boolean StopMembers(String groupAlias) throws RemoteException;
    
    //public boolean AllowMembers(String groupAlias) throws RemoteException;
    
    public LinkedList<String> ListMembers(String groupAlias) throws RemoteException;
    
    public LinkedList<String> ListGroup() throws RemoteException;
}
