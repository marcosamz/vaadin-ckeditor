// CKEditor for Vaadin- Widget linkage for using CKEditor within a Vaadin application.
// Copyright (C) 2010 Yozons, Inc.
//
// This software is released under the Apache License 2.0 <http://www.apache.org/licenses/LICENSE-2.0.html>
//
// This software was originally based on the Vaadin incubator component TinyMCEEditor written by Matti Tahvonen.
//
package org.vaadin.openesignforms.ckeditor.widgetset.client.ui;

import java.util.HashMap;
import java.util.Set;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Client side CKEditor widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VCKEditorTextField extends Widget implements Paintable, CKEditorService.CKEditorListener {
	
	/** Set the CSS class name to allow styling. */
	public static final String CLASSNAME = "v-ckeditortextfield";
	
	public static final String ATTR_IMMEDIATE = "immediate";
	public static final String ATTR_READONLY = "readonly";
	public static final String ATTR_INPAGECONFIG = "inPageConfig";
	public static final String ATTR_WRITERRULES_TAGNAME = "writerRules.tagName";
	public static final String ATTR_WRITERRULES_JSRULE = "writerRules.jsRule";
	public static final String VAR_TEXT = "text";
	
	private static boolean initializedCKEDITOR = false;

	/** The client side widget identifier */
	protected String paintableId;

	/** Reference to the server connection object. */
	protected ApplicationConnection clientToServer;
	
	private String dataBeforeEdit = null;

	private boolean immediate;
	private boolean readOnly;
	
	private CKEditor ckEditor = null;
	private boolean ckEditorIsReady = false;
	
	private HashMap<String,String> writerRules = null;

	/**
	 * The constructor should first call super() to initialize the component and
	 * then handle any initialization relevant to Vaadin.
	 */
	public VCKEditorTextField() {
		// Any one-time library initializations go here
		if ( ! initializedCKEDITOR ) {
			CKEditorService.overrideBlurToForceBlur();
			initializedCKEDITOR = true;
		}

		// CKEditor prefers a textarea, but found too many issues trying to use createTextareaElement() instead of a simple div, 
		// which is okay in Vaadin where an HTML form won't be used to send the data back and forth.
		setElement(Document.get().createDivElement());

		// This method call of the Paintable interface sets the component
		// style name in DOM tree
		setStyleName(CLASSNAME);
	}
	
	/**
	 * Called whenever an update is received from the server
	 */
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		clientToServer = client;
		paintableId = uidl.getId();
		
		// This call should be made first.
		// It handles sizes, captions, tooltips, etc. automatically.
		// If clientToServer.updateComponent returns true there have been no changes
		// and we do not need to update anything.
		if ( clientToServer.updateComponent(this, uidl, true) ) {
			return;
		}
		
		if ( uidl.hasAttribute(ATTR_IMMEDIATE) ) {
	 		immediate = uidl.getBooleanAttribute(ATTR_IMMEDIATE);
		}
		if ( uidl.hasAttribute(ATTR_READONLY) ) {
			readOnly = uidl.getBooleanAttribute(ATTR_READONLY);
		}
		if ( uidl.hasVariable(VAR_TEXT) ) {
			dataBeforeEdit = uidl.getStringVariable(VAR_TEXT);
		}
		
		// Save the client side identifier (paintable id) for the widget
		if ( ! paintableId.equals(getElement().getId()) ) {
			getElement().setId(paintableId);
		}
		
		if ( readOnly ) {
			if ( ckEditor != null ) {
				dataBeforeEdit = ckEditor.getData();
				ckEditor.destroy(true);
				ckEditorIsReady = false;
				ckEditor = null;
			}
			getElement().setInnerHTML(dataBeforeEdit);
		}
		else if ( ckEditor == null ) {
			getElement().setInnerHTML(""); // in case we put contents in there while in readonly mode
			
			String inPageConfig = uidl.hasAttribute(ATTR_INPAGECONFIG) ? uidl.getStringAttribute(ATTR_INPAGECONFIG) : null;
			
			// See if we have any writer rules
			int i = 0;
			while( true ) {
				if ( ! uidl.hasAttribute(ATTR_WRITERRULES_TAGNAME+i)  ) {
					break;
				}
				// Save the rules until our instance is ready
				String tagName = uidl.getStringAttribute(ATTR_WRITERRULES_TAGNAME+i);
				String jsRule  = uidl.getStringAttribute(ATTR_WRITERRULES_JSRULE+i);
				if ( writerRules == null ) {
					writerRules = new HashMap<String,String>();
				}
				writerRules.put(tagName, jsRule);
				++i;
			}
			
			ckEditor = (CKEditor)CKEditorService.loadEditor(paintableId, this, inPageConfig);
			
			// editor data and some options are set when the instance is ready....
		} else if ( ckEditorIsReady ) {
			if ( dataBeforeEdit != null ) {
				ckEditor.setData(dataBeforeEdit);
			}
		}
		
	}

	// Listener callback
	@Override
	public void onSave() {
		if ( ckEditorIsReady ) {
			// Called if the user clicks the Save button. 
			String data = ckEditor.getData();
			if ( ! data.equals(dataBeforeEdit) ) {
				clientToServer.updateVariable(paintableId, VAR_TEXT, data, true);
				dataBeforeEdit = data; // update our image since we sent it to the server
			}
		}
	}

	// Listener callback
	@Override
	public void onBlur() {
		if ( ckEditorIsReady ) {
			boolean sendToServer = false;
			
			if ( clientToServer.hasEventListeners(this, EventId.BLUR) ) {
				sendToServer = true;
	            clientToServer.updateVariable(paintableId, EventId.BLUR, "", false);
			}
			
			String data = ckEditor.getData();
			if ( ! data.equals(dataBeforeEdit) ) {
				clientToServer.updateVariable(paintableId, VAR_TEXT, data, false);
	            if (immediate) {
	            	sendToServer = true;
	            	dataBeforeEdit = data; // let's only update our image if we're going to send new data to the server
	            }
			}
			
	        if (sendToServer) {
	            clientToServer.sendPendingVariableChanges();
	        }
		}
	}

	// Listener callback
	@Override
	public void onFocus() {
		if ( ckEditorIsReady ) {
			if ( clientToServer.hasEventListeners(this, EventId.FOCUS) ) {
	            clientToServer.updateVariable(paintableId, EventId.FOCUS, "", true);
			}
		}
	}

	// Listener callback
	@Override
	public void onInstanceReady() {
		ckEditor.instanceReady(this);
		
		if ( writerRules != null ) {
			Set<String> tagNameSet = writerRules.keySet();
			for( String tagName : tagNameSet ) {
				ckEditor.setWriterRules(tagName, writerRules.get(tagName));
			}
			writerRules = null; // don't need them anymore
		}
		
		if ( dataBeforeEdit != null ) {
			ckEditor.setData(dataBeforeEdit);
		}
		
		ckEditorIsReady = true;
	}
	
	@Override
	public void setWidth(String width) {
		super.setWidth(width);
	}
	
	@Override
	public void setHeight(String height) {
		if ( height == null || "".equals(height) ) { // such maximized height is not supported by CKEditor, so we go back to our default
			height = "300px";
		}
		super.setHeight(height);
	}

}