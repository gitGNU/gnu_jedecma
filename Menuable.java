/*  
 * interface Menuable - methods for programs executed via menu
 * 
 * Copyright (c) 2017 Stefano Marchetti
 * 
 * This file is part of Jedecma - breast ultrasound examinations archiving software
 * 
 * Jedecma is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Jedecma is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jedecma.  If not, see <http://www.gnu.org/licenses/>.
 *  
*/

/* interface Menuable - studio di applicazione interfaccia
   le classi attivate da voci di menu' per le quali e' prevista la condivisione
   dello stesso pannello principale, devono poter essere terminate dal menu' alla selezione 
   di una nuova voce di menu'.
*/


package jedecma;

public interface Menuable {
   
   void start();  // attiva la classe
   
   void stop();   // termina la classe: la classe 'sa' come fare

} // end
