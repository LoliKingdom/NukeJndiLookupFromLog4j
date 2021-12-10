# NukeJndiLookupFromLog4j
Removal of JndiLookup in now obsolete Minecraft versions, or versions that still have log4j &lt; 2.10 and is unable to use `-Dlog4j2.formatMsgNoLookups=true`.

This is needed because of a major vulnerability introduced by the class' functionality, see more here: https://github.com/apache/logging-log4j2/pull/608

- [Java Application](https://github.com/LoliKingdom/NukeJndiLookupFromLog4j/releases/tag/java_app): resides in this repository (see releases), that removes JndiLookup.class from any log4j builds you feed via a GUI. Hard removal of the class on the server-side forcibly closing the vulnerability.

- [Forge Mod](https://github.com/LoliKingdom/NukeJndiLookupFromLog4j/tree/master/ForgeMod) [(CurseForge Link)](https://www.curseforge.com/minecraft/mc-mods/nukejndilookupfromlog4j): A Minecraft mod developed for MinecraftForge for Minecraft versions 1.12.2 and lower, a softer, but hacky fix than the aforementioned method.