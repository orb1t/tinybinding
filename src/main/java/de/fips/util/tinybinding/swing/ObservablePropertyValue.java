/*
 * Copyright © 2010-2011 Philipp Eichhorn.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.fips.util.tinybinding.swing;

import static org.fest.reflect.core.Reflection.*;
import static de.fips.util.tinybinding.WeakListeners.addWeak;
import static de.fips.util.tinybinding.util.Cast.uncheckedCast;
import static de.fips.util.tinybinding.util.Reflection.*;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fest.reflect.beanproperty.Invoker;
import org.fest.reflect.exception.ReflectionError;

import de.fips.util.tinybinding.util.Cast;

/**
 * {@link de.fips.util.tinybinding.impl.ObservableValue ObservableValue} that can wrap any named property of a Swing Component.
 * <p>
 * <b>Note:</b> All used listeners are added as a {@link java.lang.ref.WeakReference WeakReferences}, so they gets
 * garbage collected when the time comes.
 *
 * @param <TYPE> Type of the observed property.
 * @see PropertyChangeListener
 * @see ChangeListener
 * @author Philipp Eichhorn
 */
class ObservablePropertyValue<TYPE> extends ObservableComponentValue<TYPE, Container> implements PropertyChangeListener, ChangeListener {
	private final String propertyName;
	private final Class<TYPE> propertyType;

	public ObservablePropertyValue(final String propertyName, final Class<TYPE> propertyType, final Container component) {
		super(component);
		this.propertyName = propertyName;
		this.propertyType = propertyType;
		try {
			addWeak(PropertyChangeListener.class, this).withPropertyName(propertyName).toTarget(getComponent());
		} catch (IllegalStateException e) {
			addWeak(PropertyChangeListener.class, this).toTargetIfPossible(getComponent());
		}
		addWeak(ChangeListener.class, this).toTargetIfPossible(getComponent());
		guardedUpdateValue();
	}

	@Override
	public void stateChanged(final ChangeEvent event) {
		guardedUpdateValue();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		guardedSetValue(Cast.<TYPE>uncheckedCast(event.getNewValue()));
	}

	@Override
	protected void guardedDoSet(final TYPE value) {
		try {
			if (value != null) getInvoker().set(value);
		} catch (ReflectionError e) {
			// ignore
		}
	}

	@Override
	public TYPE getComponentValue() {
		try {
			return getInvoker().get();
		} catch (ReflectionError e) {
			return null;
		}
	}

	private Invoker<TYPE> getInvoker() {
		try {
			return property(propertyName).ofType(propertyType).in(getComponent());
		} catch (ReflectionError e) {
			if (hasPrimitive(propertyType)) {
				final Invoker<TYPE> invoker = uncheckedCast(property(propertyName).ofType(getPrimitive(propertyType)).in(getComponent()));
				return invoker;
			} else throw e;
		}
	}
}