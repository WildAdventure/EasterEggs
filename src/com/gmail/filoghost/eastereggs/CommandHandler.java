/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.eastereggs;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wild.api.command.CommandFramework;
import wild.api.command.CommandFramework.Permission;

@Permission("eastereggs.admin")
public class CommandHandler extends CommandFramework {

	private EasterEggs plugin;
	
	public CommandHandler(EasterEggs plugin, String label) {
		super(plugin, label);
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		if (args.length == 0) {
			sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Comandi Easter Egg:");
			sender.sendMessage("" + ChatColor.GRAY + "/eastereggs add");
			sender.sendMessage("" + ChatColor.GRAY + "/eastereggs remove");
			sender.sendMessage("" + ChatColor.GRAY + "/eastereggs list");
			sender.sendMessage("" + ChatColor.GRAY + "/eastereggs reload");
			return;
		}
		
		if (args[0].equalsIgnoreCase("add")) {
			Player player = CommandValidate.getPlayerSender(sender);
			Block block = player.getTargetBlock((Set<Material>) null, 20);
			CommandValidate.notNull(block, "Non stai guardando nessun blocco.");
			CommandValidate.isTrue(block.getType() == Material.SKULL, "Non stai guardando un testa.");
			CommandValidate.isTrue(!plugin.isEasterEgg(block), "Blocco già segnato come easter egg!");
			
			plugin.addEasterEgg(block);
			plugin.saveToDisk();
			sender.sendMessage(ChatColor.GREEN + "Easter egg aggiunto.");
			return;
		}
		
		if (args[0].equalsIgnoreCase("remove")) {
			Player player = CommandValidate.getPlayerSender(sender);
			Block block = player.getTargetBlock((Set<Material>) null, 20);
			CommandValidate.notNull(block, "Non stai guardando nessun blocco.");
			CommandValidate.isTrue(plugin.isEasterEgg(block), "Quel blocco non è un easter egg.");
			
			plugin.removeEasterEgg(block);
			plugin.saveToDisk();
			sender.sendMessage(ChatColor.GREEN + "Easter egg rimosso.");
			return;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			sender.sendMessage(ChatColor.GREEN + "Easter Egg attivi (" + plugin.getEasterEggs().size() + "):");
			for (Egg egg : plugin.getEasterEggs()) {
				sender.sendMessage(ChatColor.GRAY + "- " + egg.getX() + ", " + egg.getY() + ", " + egg.getZ() + " (mondo: " + egg.getWorldName() + ")");
			}
			return;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			plugin.load();
			sender.sendMessage(ChatColor.GREEN + "Configurazione ricaricata.");
			return;
		}
		
		sender.sendMessage(ChatColor.RED + "Sub comando sconosciuto.");
	}

}
