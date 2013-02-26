/*
 * This file is part of DropletCooldown.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.spout.droplet.cooldown;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import org.spout.api.Server;
import org.spout.api.UnsafeMethod;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.entity.Player;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.plugin.CommonPlugin;

import org.spout.droplet.cooldown.command.DropletCommand;
import org.spout.droplet.cooldown.configuration.DropletConfig;

public class DropletCooldown extends CommonPlugin {
	private DropletConfig config;
	private Server server;

	private HashSet<UUID> Players = new HashSet<UUID>();

	@Override
	@UnsafeMethod
	public void onDisable() {
		server.getScheduler().cancelTasks(this);
	}

	@Override
	@UnsafeMethod
	public void onEnable() {
		try {
			config.load();
		} catch (ConfigurationException e) {
			getLogger().log(Level.WARNING, "Error loading DropletCooldown configuration: ", e);
		}
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());
		getEngine().getRootCommand().addSubCommands(this, DropletCommand.class, commandRegFactory);

	}

	@Override
	public void onLoad() {
		server = (Server) getEngine();
		config = new DropletConfig(getDataFolder());
	}

	public Server getServer() {
		return server;
	}

	public void addPlayer(Player ply) {
		if (Players.contains(ply.getUID())) {
			return;
		} else {
			Players.add(ply.getUID());
		}
	}

	public boolean containsPlayer(Player ply) {
		if (Players.contains(ply.getUID())) {
			return true;
		} else {
			return false;
		}
	}

	public void removePlayer(Player ply) {
		if (Players.contains(ply.getUID())) {
			Players.remove(ply.getUID());
		} else {
			return;
		}
	}

	public DropletConfig getConfig() {
		return config;
	}
}
