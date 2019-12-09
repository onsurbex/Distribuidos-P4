/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.io.Serializable;

/**
 *
 * @author onsur
 */
public class GroupMember implements Serializable{
    String alias;
    String hostname;
    int memberID;
    int groupID;
    int nport;
    
    /**
     *
     * @param alias Name of the group
     * @param hostname Name of the host
     * @param memberID Member ID
     * @param groupID Group ID
     * @param nport
     */
    public GroupMember(String alias, String hostname, int memberID, int groupID, int nport){
        this.alias = alias;
        this.hostname = hostname;
        this.memberID = memberID;
        this.groupID = groupID;  
        this.nport = nport;
    }    
}
