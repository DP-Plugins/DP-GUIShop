<center><img src="https://i.postimg.cc/MKPVVR1s/dplogo-512.png" alt="logo"></center>
<center><img src="https://i.postimg.cc/RZ9dqPFx/introduce.png" alt="introduce"></center>

DP-GUIShop is a Minecraft plugin that allows server admins to easily create and manage in-game shops via an intuitive GUI.  
It provides a simple way to configure shop items and prices through menus, eliminating the need to edit files manually.  
Players can buy or sell items in these shops using the server’s economy (MoneyAPI via DPP-Core), while admins have control over shop availability and access permissions.

---

<center><img src="https://i.postimg.cc/RZ9dqP08/description.png" alt="description"></center>

- **GUI-Based Configuration** – Create and edit shops entirely through in-game menus  
- **Shop Enable / Disable** – Toggle each shop on or off without deleting its data  
- **Pagination Support** – Manage large shops using multiple pages (page index starts at 0)  
- **Per-Shop Permissions** – Restrict access to specific shops using permission nodes  
- **Custom Language Support** – All messages are configurable via language files  

---

<center><img src="https://i.postimg.cc/rwcjzhpH/depend-plugin.png" alt="depend-plugin"></center>

- All DP-Plugins require the **`DPP-Core`** plugin  
- The plugin will not work if **`DPP-Core`** is not installed  
- Download **`DPP-Core`** here: <a href="https://github.com/DP-Plugins/DPP-Core/releases" target="_blank">Click me!</a>  
- An economy system is required (EssentialsX's economy system)  

---

<center><img src="https://i.postimg.cc/dV01RxJB/installation.png" alt="installation"></center>

1️⃣ Place **`DPP-Core`** and **`DP-GUIShop-*.jar`** into your server’s **`plugins`** folder  

2️⃣ Start or restart the server  
   (Config, language, and shop data files will be generated automatically)

3️⃣ If needed, edit **`config.yml`** or language files and reload using `/shop reload`  

---

<center><img src="https://i.postimg.cc/jSKcC85K/settings.png" alt="settings"></center>

- **`config.yml`**  
  - Plugin prefix  
  - Language selection (`Lang: en_US`, `ko_KR`, etc.)

- **`shops/` folder**  
  - Stores individual shop data files (auto-managed)

---

<center><img src="https://i.postimg.cc/SxqdjZKw/command.png" alt="command"></center>

❗ Some commands require admin permission (`dpgs.admin`)

**Command List and Examples**

| Command | Permission | Description | Example |
|------|------------|-------------|---------|
| `/shop create <name> <size>` | dpgs.admin | Create a shop (size = rows, 2–6) | `/shop create Store 6` |
| `/shop title <name> <title>` | dpgs.admin | Set shop title | `/shop title Store &aMy Shop` |
| `/shop maxpage <name> <maxPage>` | dpgs.admin | Set max page number | `/shop maxpage Store 3` |
| `/shop items <name>` | dpgs.admin | Edit shop items via GUI | `/shop items Store` |
| `/shop price <name>` | dpgs.admin | Set buy/sell prices | `/shop price Store` |
| `/shop enable <name>` | dpgs.admin | Enable a shop | `/shop enable Store` |
| `/shop disable <name>` | dpgs.admin | Disable a shop | `/shop disable Store` |
| `/shop delete <name>` | dpgs.admin | Delete a shop | `/shop delete Store` |
| `/shop permission <name> <node>` | dpgs.admin | Set shop permission | `/shop permission Store vip.use` |
| `/shop removepermission <name>` | dpgs.admin | Remove shop permission | `/shop removepermission Store` |
| `/shop list` | dpgs.admin | List all shops | `/shop list` |
| `/shop reload` | dpgs.admin | Reload plugin config | `/shop reload` |
| `/shop open <name>` | None | Open a shop | `/shop open Store` |

**❗Notes when using commands**

- Shop names support Korean and English, but **spaces are not allowed**  
- Shop edits (items/prices) are saved automatically when closing the GUI  
- Players must have the required permission if one is set for the shop  
- Admin commands require **OP** or `dpgs.admin` permission  

---

<center><img src="https://i.postimg.cc/Z5ZH0fqL/api-integration.png" alt="api-integration"></center>

- this plugin is using EssentialsX's economy system.

---

<center><a href="https://discord.gg/JnMCqkn2FX"><img src="https://i.postimg.cc/4xZPn8dC/discord.png" alt="discord"></a></center>

- https://discord.gg/JnMCqkn2FX  
- For questions, bug reports, or feature suggestions, please join our Discord  
- Feedback and improvement ideas are always welcome!

---
