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
package org.spout.droplet.cooldown.command;

import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.inventory.ItemStack;
import org.spout.api.scheduler.TaskPriority;
import org.spout.vanilla.component.entity.inventory.PlayerInventory;
import org.spout.vanilla.material.VanillaMaterials;

import org.spout.droplet.cooldown.DropletCooldown;

public class DropletCommand {
	// We are going to call on the main class so we can use methods/objects from the class.
	private DropletCooldown plugin;

	public DropletCommand(DropletCooldown plugin) {
		this.plugin = plugin;
	}

	// Aliases are like nicknames for commands. You can use them to make the command shorter.
	@SuppressWarnings("static-access")
	@Command(aliases = {"stone", "s"}, usage = "/stone", desc = ".", min = 0, max = 0)
	@Permissible("dc.commands.stone")
	public void stone(CommandSource source, CommandArguments args) throws CommandException {
		if (source instanceof Player) {
			// We are now going to cast the source as a player object since we confirmed that the source is the player.
			final Player ply = (Player) source;
			// Now we are going to add the players UUID to our list. So we can make sure the same player doesn't repeat the command.
			if (plugin.containsPlayer(ply)) {
				ply.sendMessage("This command is still on cooldown for you!");
				return;
			} else {
				plugin.addPlayer(ply);
				// Now we get the players inventory and add the itemstack to it.
				ply.get(PlayerInventory.class).add(new ItemStack(VanillaMaterials.STONE, 64));
				ply.sendMessage("64 stone has been added to your inventory!");

				// Now we are going to start the task
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					@Override
					public void run() {
						// After 30 seconds we're going to tell the player he/she can use the commmand again and remove them from the ArrayList.
						plugin.removePlayer(ply);
						ply.sendMessage("You can now use the command 'stone' again!");
					}
				}, plugin.getConfig().COMMANDDELAY.getLong() * 20, TaskPriority.LOW);
			}
			source.sendMessage("Sorry, this command can only be used by a player.");
		} else {
		}
	}
}
