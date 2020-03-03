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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.val;
import wild.api.WildCommons;

public class EasterEggs extends JavaPlugin {

	@Getter private Set<Egg> easterEggs;
	private Map<Egg, Set<String>> collectedEggs;

	@Getter private List<String> commands;
	@Getter private String broadcast, privateMsg, alreadyCollectedError;

	@Override
	public void onEnable() {
		easterEggs = Sets.newHashSet();
		collectedEggs = Maps.newHashMap();
		
		Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
		new CommandHandler(this, "eastereggs");
		load();
	}
	
	public void load() {
		saveDefaultConfig();
		reloadConfig();
		
		easterEggs.clear();
		collectedEggs.clear();
		
		commands = getConfig().getStringList("commands");
		broadcast = WildCommons.color(getConfig().getString("broadcast"));
		privateMsg = WildCommons.color(getConfig().getString("private"));
		alreadyCollectedError =  WildCommons.color(getConfig().getString("alreadyCollected"));
		
		int loaded = 0;
		if (getConfig().getStringList("eggs") != null) {
			for (String entry : getConfig().getStringList("eggs")) {
				try {
					easterEggs.add(Egg.deserialize(entry));
					loaded++;
				} catch (Exception e) {
					getLogger().warning("Blocco non valido: " + entry);
				}
			}
		}
		
		for (Egg egg : easterEggs) {
			List<String> players = getConfig().getStringList("collected." + egg.serialize());
			if (players != null) {
				collectedEggs.put(egg, Sets.newHashSet(players));
			}
		}
		
		getLogger().info("Caricati " + loaded + " blocchi.");
	}
	
	public void saveToDisk() {
		List<String> serializedEggs = Lists.newArrayList();
		
		for (Egg egg : easterEggs) {
			serializedEggs.add(egg.serialize());
		}
		
		getConfig().set("eggs", serializedEggs);
		
		for (val entry : collectedEggs.entrySet()) {
			getConfig().set("collected." + entry.getKey().serialize(), Lists.newArrayList(entry.getValue()));
		}
		
		saveConfig();
	}
	
	public boolean hasCollected(Player collector, Egg egg) {
		Set<String> players = collectedEggs.get(egg);
		return players != null && players.contains(collector.getName().toLowerCase());
	}
	
	public void setCollected(Player collector, Egg egg) {
		Set<String> players = collectedEggs.get(egg);
		if (players == null) {
			players = Sets.newHashSet();
			collectedEggs.put(egg, players);
		}
		
		players.add(collector.getName().toLowerCase());
	}
	
	public void addEasterEgg(Block block) {
		easterEggs.add(new Egg(block));
	}
	
	public void removeEasterEgg(Block block) {
		easterEggs.remove(new Egg(block));
	}
	
	public boolean isEasterEgg(Block block) {
		return getEasterEgg(block) != null;
	}

	public Egg getEasterEgg(Block block) {
		if (block != null && block.getType() == Material.SKULL) {
			Egg egg = new Egg(block);
			if (easterEggs.contains(egg)) {
				return egg;
			}
		}
		
		return null;
	}

}
