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

DP-GUIShop is a Minecraft plugin that allows for easy creation and management of shops on servers. It offers intuitive item and price configuration through a GUI, along with features for enabling/disabling shops and pagination.

## Plugin Features
- **GUI-Based Configuration**: Easily set items and prices using a graphical interface.
- **Shop Enable/Disable**: Activate or deactivate specific shops as needed.
- **Pagination**: Organize shops across multiple pages (pages start from 0).
- **Permission Settings**: Set or remove access permissions for individual shops.
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

## Usage Examples
- Create a shop: `/shop create myshop`
- Set shop pages: `/shop pages myshop 3`
- Open item configuration GUI: `/shop items myshop`
- Open price configuration GUI: `/shop price myshop`
- Open a shop: `/shop open myshop`

<br>
<br>

![](https://dpnw.site/assets/img/desc_card/screenshot.jpg)

![](https://dpnw.site/assets/img/screenshot/DP-GUIShop/1.jpg)

![](https://dpnw.site/assets/img/screenshot/DP-GUIShop/2.jpg)
