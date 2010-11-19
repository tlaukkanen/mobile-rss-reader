//--Need to modify--#preprocess
/*
 * Copyright (c) 2001-2005 Todd C. Stellanova, rawthought
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. 
 * 
 *
 * Created on June 17, 2003, 12:23 PM
 * This software was originally modified no later than Sept 25, 2007.
 */
/*
 * IB 2010-08-15 1.11.5Dev8 Remove unused reqSetVisible.
 * IB 2010-09-27 1.11.5Dev8 Remove unused import.
 * IB 2010-10-12 1.11.5Dev8 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-19 1.11.5Dev14 Have displayDbgMsg and be in DTEST.
 */

// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define test define
//#define DNOTEST

//#ifdef DJSR75
//@package org.kablog.kgui;
//@
//@import javax.microedition.lcdui.*;
//@
//@
//@/**
//@ *
//@ * @author  Todd C. Stellanova
//@ * @version
//@ */
//@public interface KViewParent {
//@    
//@    /**
//@     * @param view -- the view that has completed operation.
//@     */
//@    void childFinished(KViewChild child);
//@    
//@    /**
//@     * We've updated the child's status.
//@     */
//@    void childStatusChanged(KViewChild child, int statusType, int status);
//@
//@    
//@    /**
//@    * @param runny Object to run at a deferred time.
//@	*/
//@	void addDeferredAction(Runnable runny);
//@    
	//#ifdef DTEST
//@    /**
//@     * Display a debug message somehow
//@     */
//@     void displayDbgMsg(String msg, AlertType type);
	//#endif
//@
//@}
//#endif
