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
package de.fips.util.tinybinding;

import static javax.swing.JOptionPane.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.fips.util.tinybinding.autobind.AutoBinder;
import de.fips.util.tinybinding.autobind.Bindable;
import de.fips.util.tinybinding.autobind.SwingBindable;

import lombok.Application;
import lombok.BoundPropertySupport;
import lombok.BoundSetter;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.SwingInvokeLater;

public class SimpleDemo implements Application {

	@SneakyThrows
	@SwingInvokeLater
	public void runApp(final String[] params) {
		final LoginForm form = new LoginForm();
		final LoginModel model = new LoginModel();
		AutoBinder.bind(model, form);
		final JFrame frame = new JFrame("Simple Demo");
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(form, BorderLayout.CENTER);
		frame.getContentPane().add(new JButton(new LoginAction(model)), BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public static class LoginAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private final LoginModel model;

		public LoginAction(final LoginModel model) {
			super("Login");
			this.model = model;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if ("user".equals(model.getLoginName()) && "1234".equals(model.getPassword())) {
				if (model.getAutologin()) {
					showMessageDialog(null, "Login succeed - Auto Login active", "Success", INFORMATION_MESSAGE);
				} else {
					showMessageDialog(null, "Login succeed", "Success", INFORMATION_MESSAGE);
				}
			} else {
				showMessageDialog(null, "Login Failed", "Failure", WARNING_MESSAGE);
			}
		}
	}

	public static class LoginForm extends JPanel {
		private static final long serialVersionUID = 7239761542065605502L;

		@SwingBindable(hint = "text")
		private final JTextField loginName = new JTextField();
		@SwingBindable(hint = "text")
		private final JPasswordField password = new JPasswordField();
		@SwingBindable(hint = "selected")
		private final JCheckBox autologin = new JCheckBox("Auto Login");

		public LoginForm() {
			super();
			setLayout(new GridLayout(3, 2));
			add(new JLabel("Login Name:"));
			add(loginName);
			add(new JLabel("Password:"));
			add(password);
			add(autologin);
		}
	}

	@BoundSetter
	@Getter
	@BoundPropertySupport
	public static class LoginModel {
		@Bindable
		private String loginName = "";
		@Bindable
		private String password = "";
		@Bindable
		private Boolean autologin = false;
	}
}
