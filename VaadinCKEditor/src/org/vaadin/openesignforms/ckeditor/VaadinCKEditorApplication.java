// CKEditor for Vaadin - Widget linkage for using CKEditor within a Vaadin application.
// Copyright (C) 2010 Yozons, Inc.
//
// This software is released under the Apache License 2.0 <http://www.apache.org/licenses/LICENSE-2.0.html>
//
// This software was originally based on the Vaadin incubator component TinyMCEEditor written by Matti Tahvonen.
//
package org.vaadin.openesignforms.ckeditor;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class VaadinCKEditorApplication extends Application {
	private static final long serialVersionUID = -3106462237499366300L;

	@Override
	public void init() {
		setTheme("ckexample"); // not needed, but here to show if you wanted to style the v-ckeditortextfield style defined in VAADIN/themes/ckexample/styles.css
		
		// Using CssLayout(), we found IE8 would lose its editors if you clicked any button after initial load.
		// However, if you loaded, then clicked RELOAD/REFRESH in IE8, it wouldn't happen.
		// Then we found it doesn't happen at all with VerticalLayout.
		// Window mainWindow = new Window("Vaadin CKEditor Application", new CssLayout());
		Window mainWindow = new Window("Vaadin CKEditor Application", new VerticalLayout());
		mainWindow.setSizeFull();
		setMainWindow(mainWindow);
		
		mainWindow.addComponent(new Button("Hit server"));


		/* This is how the default Full toolbar is defined, showing you all the options available for defining your toolbar.
[
  ['Source','-','Save','NewPage','Preview','-','Templates'],
  ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Print', 'SpellChecker', 'Scayt'],
  ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
  ['Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField'],
  '/',
  ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
  ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
  ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
  ['Link','Unlink','Anchor'],
  ['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak'],
  '/',
  ['Styles','Format','Font','FontSize'],
  ['TextColor','BGColor'],
  ['Maximize', 'ShowBlocks','-','About']
]
		 */
		
		CKEditorConfig config1 = new CKEditorConfig();
		config1.useCompactTags();
		config1.disableElementsPath();
		config1.setResizeDir(CKEditorConfig.RESIZE_DIR.HORIZONTAL);
		config1.disableSpellChecker();
		config1.setToolbarCanCollapse(false);
		config1.enableTableResizePlugin();
		config1.setHeight("300px");
		
		final CKEditorTextField ckEditorTextField1 = new CKEditorTextField(config1);
		ckEditorTextField1.setHeight(410,Sizeable.UNITS_PIXELS); // account for 300px editor plus toolbars
		mainWindow.addComponent(ckEditorTextField1);
		
		final String editor1InitialValue = 
			"<p>Thanks TinyMCEEditor for getting us started on the CKEditor integration.</p><h1>Like TinyMCEEditor said, &quot;Vaadin rocks!&quot;</h1><h1>And CKEditor is no slouch either.</h1>";

		ckEditorTextField1.setValue(editor1InitialValue);
		ckEditorTextField1.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 54555014513845952L;

			public void valueChange(ValueChangeEvent event) {
				getMainWindow().showNotification("CKEditor #1 contents: " + event.getProperty().toString().replaceAll("<", "&lt;"));
			}
		});
		
		Button resetTextButton1 = new Button("Reset editor #1");
		resetTextButton1.addListener( new Button.ClickListener() {			
			private static final long serialVersionUID = -5568890922280949248L;

			@Override
			public void buttonClick(ClickEvent event) {
				if ( ! ckEditorTextField1.isReadOnly() ) {
					ckEditorTextField1.setValue(editor1InitialValue);
				}
			}
		});
		mainWindow.addComponent(resetTextButton1);
		
		Button toggleReadOnlyButton1 = new Button("Toggle read-only editor #1");
		toggleReadOnlyButton1.addListener( new Button.ClickListener() {			
			private static final long serialVersionUID = -2447367323473949937L;

			@Override
			public void buttonClick(ClickEvent event) {
				ckEditorTextField1.setReadOnly( ! ckEditorTextField1.isReadOnly() );
			}
		});
		mainWindow.addComponent(toggleReadOnlyButton1);

		
		// Now add in a second editor....
		final String editor2InitialValue = 
			"<p>Here is editor #2.</p><h1>Hope you find this useful in your Vaadin projects.</h1>";

		final CKEditorTextField ckEditorTextField2 = new CKEditorTextField();
		mainWindow.addComponent(ckEditorTextField2);
		
		CKEditorConfig config2 = new CKEditorConfig();
		config2.setInPageConfig(
				"{ " +
				    "width: '600px'," +  // example to show you can use CKEditor's width setting too in the config if CSS is not wanted
				    "extraPlugins: 'vaadinsave'," + // add this if using the editor's VaadinSave button
				    "removePlugins: 'scayt'," + // use this to remove the built in spell checker
					"toolbar : 'Custom'," +
					"toolbar_Custom : [" +
						"['Styles','-','VaadinSave']" +
									 "]" +
				" }" 
				     			);
		ckEditorTextField2.setConfig(config2);
		ckEditorTextField2.setValue(editor2InitialValue);
		
		ckEditorTextField2.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 5261774097251439369L;

			public void valueChange(ValueChangeEvent event) {
				getMainWindow().showNotification("CKEditor #2 contents: " + event.getProperty().toString().replaceAll("<", "&lt;"));
			}
		});
		
		Button resetTextButton2 = new Button("Reset editor #2");
		resetTextButton2.addListener( new Button.ClickListener() {			
			private static final long serialVersionUID = 8516010622125693968L;

			@Override
			public void buttonClick(ClickEvent event) {
				if ( ! ckEditorTextField2.isReadOnly() ) {
					ckEditorTextField2.setValue(editor2InitialValue);
				}
			}
		});
		mainWindow.addComponent(resetTextButton2);
		
		Button toggleReadOnlyButton2 = new Button("Toggle read-only editor #2");
		toggleReadOnlyButton2.addListener( new Button.ClickListener() {			
			private static final long serialVersionUID = -6120226593606697001L;

			@Override
			public void buttonClick(ClickEvent event) {
				ckEditorTextField2.setReadOnly( ! ckEditorTextField2.isReadOnly() );
			}
		});
		mainWindow.addComponent(toggleReadOnlyButton2);


	}
	
	@Override
	public String getVersion() {
		return "0.7";
	}

}
