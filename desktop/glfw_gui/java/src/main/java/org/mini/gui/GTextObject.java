/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.gui;

import org.mini.glfm.Glfm;
import org.mini.glfw.Glfw;
import org.mini.gui.event.GFocusChangeListener;
import org.mini.gui.event.GStateChangeListener;

import static org.mini.nanovg.Gutil.toUtf8;

/**
 * @author Gust
 */
public abstract class GTextObject extends GObject implements GFocusChangeListener {

    String hint;
    byte[] hint_arr;
    StringBuilder textsb = new StringBuilder();
    byte[] text_arr;
    boolean editable = true;

    GStateChangeListener stateChangeListener;


    boolean selectMode = false;

    GObject unionObj;//if this object exists, the keyboard not disappear

    public void setHint(String hint) {
        this.hint = hint;
        hint_arr = toUtf8(hint);
    }

    public String getHint() {
        return hint;
    }

    abstract void onSetText(String text);

    public void setText(String text) {
        this.textsb.setLength(0);
        if (text != null) {
            this.textsb.append(text);
        }
        onSetText(text);
        text_arr = null;
        doStateChange();
    }

    public String getText() {
        return textsb.toString();
    }

    public void insertTextByIndex(int index, char ch) {
        textsb.insert(index, ch);
        text_arr = null;
        doStateChange();
    }

    public void deleteTextByIndex(int index) {
        textsb.deleteCharAt(index);
        text_arr = null;
        doStateChange();
    }

    public void deleteAll() {
        textsb.setLength(0);
        text_arr = null;
        doStateChange();
    }

    abstract public String getSelectedText();

    abstract public void deleteSelectedText();

    abstract public void insertTextAtCaret(String str);

    abstract void resetSelect();

    public void doSelectText() {

    }

    public void doSelectAll() {

    }

    public void doCopyClipBoard() {
        String s = getSelectedText();
        if (s != null) {
            Glfm.glfmSetClipBoardContent(s);
            Glfw.glfwSetClipboardString(getForm().getWinContext(), s);
        }
    }

    public void doCut() {
        doCopyClipBoard();
        deleteSelectedText();
    }

    public void doPasteClipBoard() {
        deleteSelectedText();
        String s = Glfm.glfmGetClipBoardContent();
        if (s == null) {
            s = Glfw.glfwGetClipboardString(getForm().getWinContext());
        }
        if (s != null) {
            insertTextAtCaret(s);
        }
    }

    long winContext;

    @Override
    public void focusGot(GObject go) {
        if (editable) {
            GForm.showKeyboard();
        }
    }

    @Override
    public void focusLost(GObject newgo) {
        if (newgo != unionObj && newgo != this) {
            GForm.hideKeyboard();
        }
        GToolkit.disposeEditMenu();
        touched = false;
    }

    boolean touched;

    @Override
    public void touchEvent(int phase, int x, int y) {
        if (isInArea(x, y)) {
            switch (phase) {
                case Glfm.GLFMTouchPhaseBegan: {
                    touched = true;
                    break;
                }
                case Glfm.GLFMTouchPhaseEnded: {
                    if (touched) {
                        if (getForm() != null && editable) {
                            //System.out.println("touched textobject");
                        }
                        touched = false;
                    }
                    break;
                }
            }
        }
        super.touchEvent(phase, x, y);
    }

    @Override
    public void longTouchedEvent(int x, int y) {
        GToolkit.callEditMenu(this, x, y);
        //System.out.println("long toucched");
        super.longTouchedEvent(x, y);
    }

    static public boolean isEditMenuShown() {
        if (GToolkit.getEditMenu() == null) {
            return false;
        }
        return GToolkit.getEditMenu().getParent() != null;
    }

    /**
     * @return the unionObj
     */
    public GObject getUnionObj() {
        return unionObj;
    }

    /**
     * @param unionObj the unionObj to set
     */
    public void setUnionObj(GObject unionObj) {
        this.unionObj = unionObj;
    }

    /**
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * @return the stateChangeListener
     */
    public GStateChangeListener getStateChangeListener() {
        return stateChangeListener;
    }

    /**
     * @param stateChangeListener the stateChangeListener to set
     */
    public void setStateChangeListener(GStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    void doStateChange() {
        if (this.stateChangeListener != null) {
            this.stateChangeListener.onStateChange(this);
        }
    }

}
