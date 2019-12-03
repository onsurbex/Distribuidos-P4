/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author onsur
 */
public class ObjectGroup {
    String groupAlias;
    int groupID;
    LinkedList<GroupMember> memberList;
    GroupMember owner;
    int counter = 0;
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    boolean locked = false;
    
    public ObjectGroup(String groupAlias, int groupID, String ownerAlias, String hostname, int nport){
        this.groupAlias = groupAlias;
        this.groupID = groupID;
        owner = new GroupMember(ownerAlias, hostname, counter, groupID, nport);
        memberList = new LinkedList<>();
        memberList.add(owner);
        this.counter++;
    }
    
    public GroupMember isMember(String memberAlias){
        this.lock.lock();
        try{ 
            for (GroupMember groupMember : memberList) {
                if(groupMember.alias.equals(memberAlias)) 
                    return groupMember;
            }
                return null;
            }
        finally{
            this.lock.unlock();
        }
    }
    
    public GroupMember addMember(String memberAlias, String hostname, int nport) throws InterruptedException{
        GroupMember m;
        this.lock.lock();
        try{            
            if(this.locked){
                try {
                    System.out.println("Espera");
                    condition.await(); 
                } catch (InterruptedException e){
                    System.out.println("Exception: " + e.toString());
                }
            }
            
            if(isMember(memberAlias) == null){                
                m = new GroupMember(memberAlias,hostname,counter,this.groupID, nport);
                this.counter++;
                this.memberList.add(m);
                return m;
            } else {
                return null;
            }
        } finally{
            this.lock.unlock();
        }
    }
    
    
    public boolean removeMember(String memberAlias) throws InterruptedException{
        this.lock.lock();
        try {
            if(this.locked){
                try {
                    condition.await();
                } catch(InterruptedException e){
                        System.out.println("Exception: " + e.toString());    
                }
            }

            if(isMember(memberAlias) != null && !memberAlias.equals(owner.alias)){
                this.memberList.remove(isMember(memberAlias));
                return true;
            } else {
                return false;
            }
        } finally {
            this.lock.unlock();
        }
    }
    
    /*void StopMembers(){
        this.lock.lock();
        try {
            this.locked = true;
            System.out.println("locked = true");
        } finally {
            this.lock.unlock();
        }
    }
    
    void AllowMembers(){
        this.lock.lock();
        try {
            this.locked = false;
            System.out.println("locked = false");
            condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }*/
    
    public boolean sendGroupMessage(GroupMember gm, byte[] msg){
        //not implemented
        return false;
    }
    
    LinkedList<String> ListMembers(){
        LinkedList<String> nameList = null;
        for (GroupMember member : this.memberList) {
            nameList.add(member.alias);
        }
        return nameList;
    }
    
}
