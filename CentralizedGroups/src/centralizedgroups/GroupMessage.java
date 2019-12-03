/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.io.Serializable;

/**
 *
 * @author verde
 */
public class GroupMessage implements Serializable{
    GroupMember sender;
    byte[] msg;
    
    public GroupMessage(GroupMember sender, byte[] msg){
        this.msg = msg;
        this.sender = sender;
    }
    
}
