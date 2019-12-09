/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centralizedgroups;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author onsur
 */
public interface ClientInterface {
    public void DepositMessage(GroupMessage m); //necesita reentrantlock
    public byte[] recieveGroupMessage(String galias);
}
