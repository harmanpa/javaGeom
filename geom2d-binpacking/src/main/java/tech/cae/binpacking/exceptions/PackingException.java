/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.cae.binpacking.exceptions;

/**
 *
 * @author peter
 */
public class PackingException extends Exception {

    public PackingException(String message) {
        super(message);
    }

    public PackingException(String message, Throwable cause) {
        super(message, cause);
    }

}
