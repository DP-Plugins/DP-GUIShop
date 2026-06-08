[ Migration DP-Shop data to DP-GUIShop ]

1. remove old DP-Shop plugin
2. put DP-GUIShop plugin
3. start server
4. stop server
5. goto DP-Shop folder to get old shops data (copy it)
6. goto DP-GUIShop folder and create new folder "migration"
7. paste old shop data into migration folder
8. start server
9. join server and use "/shop migration" command
10. done!


![](https://dpnw.site/assets/img/logo_white.png)

![](https://dpnw.site/assets/img/desc_card/dppcore.jpg)

# ALL DP-Plugins depend on the [DPP-Core](https://dpnw.site/plugin.html?plugin=DPP-Core) plugin. <br>Please make sure to install [DPP-Core](https://dpnw.site/plugin.html?plugin=DPP-Core). </h1>

# Discord
### Join our Discord server to get support and stay updated with the latest news and updates.

### if any questions or suggestions, please join our Discord server.

### if you find any bugs, please report them using inquiry channel.

<span style="font-size: 18px;">**Discord Invite : https://discord.gg/JnMCqkn2FX**</span>

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/desc.jpg)

# DP-GUIShop Plugin Introduction

DP-GUIShop is a Minecraft plugin that allows for easy creation and management of shops on servers. It offers intuitive item and price configuration through a GUI, along with features for enabling/disabling shops, pagination, and per-item purchase limits.

## Plugin Features
- **GUI-Based Configuration**: Easily set items and prices using a graphical interface.
- **Shop Enable/Disable**: Activate or deactivate specific shops as needed.
- **Pagination**: Organize shops across multiple pages (pages start from 0).
- **Permission Settings**: Set or remove access permissions for individual shops.
- **Purchase Limit System**: Limit how many times an item can be purchased per player or globally.
  - **world** type: Total purchase count shared across all players.
  - **perplayer** type: Each player has their own individual purchase count.
  - Remaining limit is displayed in each item's lore.
  - Limit data can be reset at any time with `/shop limitreset`.
- **DLang Support**: You can freely edit language files.

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/cmd-perm.jpg)

## Commands
| Command | Description |
|---------|-------------|
| `/shop create <name> <row>` | Creates a new shop. |
| `/shop title <name> <title>` | Sets the title of a shop. |
| `/shop maxpage <name> <maxPage>` | Sets the maximum number of pages for a shop (pages start from 0). |
| `/shop items <name> [page]` | Opens the item configuration GUI. |
| `/shop price <name> [page]` | Opens the price configuration GUI. |
| `/shop enable <name>` | Enables a shop. |
| `/shop disable <name>` | Disables a shop. |
| `/shop delete <name>` | Deletes a shop. |
| `/shop reload` | Reloads the configuration file. |
| `/shop permission <name> <node>` | Sets a permission for a shop. |
| `/shop delpermission <name>` | Removes a permission from a shop. |
| `/shop open <name>` | Opens a shop (usable by players). |
| `/shop limit <name>` | Opens the purchase limit configuration GUI. |
| `/shop limitenable <name>` | Enables the purchase limit system for a shop. |
| `/shop limitdisable <name>` | Disables the purchase limit system for a shop. |
| `/shop limittype <name> <world\|perplayer>` | Sets the limit type (`world` = shared, `perplayer` = per player). |
| `/shop limitreset <name>` | Resets all purchase limit data for a shop immediately. |
| `/shop autoresetenable <name>` | Enables scheduled auto-reset of purchase limit data. |
| `/shop autoresetdisable <name>` | Disables scheduled auto-reset. |
| `/shop autoresettime <name> <HH:mm>` | Sets the daily auto-reset time (default: `00:00`). |

## Purchase Limit System

The purchase limit system allows you to restrict how many times each item in a shop can be bought.

### How to set up:
1. **Enable the limit system**: `/shop limitenable <shopname>`
2. **Set the limit type**: `/shop limittype <shopname> world` or `/shop limittype <shopname> perplayer`
3. **Open limit configuration GUI**: `/shop limit <shopname>`
4. **Click an item** in the GUI → enter the limit amount in chat → the limit is saved
   - Enter `0` to remove the limit for that item
5. The remaining purchase count is automatically displayed in the item's lore in the shop.

### Limit Types:
| Type | Description |
|------|-------------|
| `world` | The total purchase count is shared by all players on the server. |
| `perplayer` | Each player has their own individual purchase count. |

## Auto-Reset System

Purchase limit data can be automatically reset at a scheduled time each day (default: midnight `00:00`). Auto-reset is **disabled by default** and must be enabled per shop.

### How it works:
- The plugin checks every **1 minute** whether any shop's scheduled reset time has been reached.
- On server **startup**, it catches up any missed resets (if the server was offline at the scheduled time).
- A broadcast message is sent to the server when a shop is auto-reset.
- Each shop stores its own auto-reset time, so different shops can reset at different times.

### Auto-Reset Commands:
| Command | Description |
|---------|-------------|
| `/shop autoresetenable <name>` | Enables daily auto-reset for the shop. |
| `/shop autoresetdisable <name>` | Disables daily auto-reset for the shop. |
| `/shop autoresettime <name> <HH:mm>` | Sets the daily reset time (e.g. `00:00`, `06:00`). Default is `00:00`. |

## Usage Examples
- Create a shop: `/shop create myshop`
- Set shop pages: `/shop pages myshop 3`
- Open item configuration GUI: `/shop items myshop`
- Open price configuration GUI: `/shop price myshop`
- Open limit configuration GUI: `/shop limit myshop`
- Enable limit for shop: `/shop limitenable myshop`
- Set limit type to per-player: `/shop limittype myshop perplayer`
- Reset limit data immediately: `/shop limitreset myshop`
- Enable auto-reset at midnight: `/shop autoresetenable myshop`
- Set auto-reset time to 6AM: `/shop autoresettime myshop 06:00`
- Disable auto-reset: `/shop autoresetdisable myshop`
- Open a shop: `/shop open myshop`

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/screenshot.jpg)

![](https://dpnw.site/assets/img/screenshot/DP-GUIShop/1.jpg)

![](https://dpnw.site/assets/img/screenshot/DP-GUIShop/2.jpg)
