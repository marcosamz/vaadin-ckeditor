// Vaadin CKEditor - Widget linkage for using CKEditor within a Vaadin application.
// Copyright (C) 2010 Yozons, Inc.
//
// This software is released under the Apache License 2.0 <http://www.apache.org/licenses/LICENSE-2.0.html>
//
// This software was originally based on the Vaadin incubator component TinyMCEEditor written by Matti Tahvonen.
//
package org.vaadin.openesignforms.ckeditor.widgetset.client.ui;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * GWT wrapper for CKEDITOR. 
 *
 */
public class CKEditorService {

	/**
	 * Use this method to load editor to given identifier.
	 * 
	 * @param id the string DOM <div> 'id' attribute value for the element you want to replace with CKEditor
	 * @param listener the CKEditorService.CKEditorListener will get notified when the editor instance is ready, changed, etc.
	 * @param jsInPageConfig the String possible custom "in page" configuration; note that this must be an expected JSON for the CKEDITOR in page config.
	 * sent "as is" without any real syntax or security testing, so be sure you know it's valid and not malicious, 
	 * such as: <code>{toolbar : 'Basic', language : 'en'}</code>
	 */
	public static native JavaScriptObject loadEditor(String id, CKEditorService.CKEditorListener listener, String jsInPageConfig)
	/*-{
	 	// Build our inPageConfig object based on the JSON jsInPageConfig sent to us.
	 	var inPageConfig = @org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditorService::convertJavaScriptStringToObject(Ljava/lang/String;)(jsInPageConfig);
	 	
	 	var myEditor;
	 	
	 	if ( inPageConfig ) {
	    	myEditor = $wnd.CKEDITOR.replace( id, inPageConfig );
	 	} else {
	    	myEditor = $wnd.CKEDITOR.replace( id );
	 	}
	 	
	 	// Whenever we lose focus, if our buffer has changed, we'll update the listener should they want to send it back the server (immediate mode).
	 	// CKEditor doesn't have an onchange type event. The 'listener' passed to us is used as 'listenerData' for the callback.
	 	myEditor.on( 'blur', function( ev ) {
	 		if ( this.checkDirty() ) { 
	 			ev.listenerData.@org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditorService.CKEditorListener::onChange()();
	 			this.resetDirty(); // Not sure if this is a good idea or not, but trying to queue to the server only once per checkDirty()
	 		}
    	}, null, listener);
    	
    	myEditor.on( 'instanceReady', function( ev ) {
    		ev.listenerData.@org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditorService.CKEditorListener::onInstanceReady()();
		}, null, listener);
		
		return myEditor;

	}-*/
	;
	
	/**
	 * Returns a javascript CKEDITOR.editor instance for given id.
	 * 
	 * @param id the String id of the editor instance
	 * @return the overlay for CKEDITOR.editor or null in not yet initialized
	 */
	public native static CKEditor get(String id)
	/*-{
		return $wnd.CKEDITOR.instances[ id ];
	}-*/;
	
	// TODO: Never tested yet
	public native static void addStylesSet(String name, String jsStyles)
	/*-{
	 	var styles = @org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditorService::convertJavaScriptStringToObject(Ljava/lang/String;)(jsStyles);
		$wnd.CKEDITOR.addStylesSet(name,styles);
	}-*/;
	
	
	// TODO: Never tested yet
	public native static void addTemplates(String name, String jsDefinition)
	/*-{
	 	var definition = @org.vaadin.openesignforms.ckeditor.widgetset.client.ui.CKEditorService::convertJavaScriptStringToObject(Ljava/lang/String;)(jsDefinition);
		$wnd.CKEDITOR.addTemplates(name,definition);
	}-*/;

	public native static JavaScriptObject convertJavaScriptStringToObject(String jsString)
	/*-{
	    try {
	 		return eval('('+jsString+')');
	 	} catch (e) { 
	 		alert('convertJavaScriptStringToObject() INVALID JAVASCRIPT: ' + jsString); 
	 		return {}; 
	 	}
	}-*/;


	/**
	 * An interface for the VCKEditorTextField to get events from the CKEditor.
	 */
	public interface CKEditorListener {
		public void onInstanceReady();
		public void onChange();
	}

}
