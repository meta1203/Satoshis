Satoshis
========

Minecraft with real money!

Ah, economy plugins... Without them, we would have no in game currency! But what if this in game currency... was also out-of-game currency?

*Enter Satoshis!*
Coupled with the Bitcoin P2P digital currency, you can trade in game with real money!

The design is simple:
1) Player starts with no money 
2) The player can add money to his account by sending Bitcoin to a certain address. 
3) The player can subtract money from his account into real life using a command. 
4) Trade takes place as usual, as the plugin links into vault. Any shop/purchase plugin supporting vault is supported here! 
5) Tax system allows for a "Sales Tax" on each money transfer. The settings on whether the buyer or the seller is held responsible for tax, and tax rates will be available in the config.

*Note!*
This plugin requires a Bitcoin server! Simply put, install bitcoind on to a box, presumably 24/7. This sounds easy, and it is, if you are running the most up to date Operating System. However, due to the way Bitcoin is designed, it will download 2-4 GB (Someone back me up on this?) of data over the web and onto you're hard drive. This is the way Bitcoin works, and it is perfectly fine. There are no viruses being downloaded and your hard drive is not dying. This also puts restrictions on who can install this plugin. Usually, you can install it on any VPS or dedicated server box. You also MIGHT be able to find a compatible Bitcoin API server out there, negating the need of installing bitcoind. Either way works.

Probable Commands:

Base: /sat, /satoshis Usage:

/sat - List current amount of Bitcoin in your minecraft account.
/sat give <player> <amount> - Transfer's money from your account to the selected player's account
/sat req <player> <amount> - Requests money from another player. Does not guarantee that you will receive the money.
/sat withdraw <address> [amount] - Transfers money from your account to your Bitcoin wallet. Must have at least the amount specified in the config. If the amount is left off, it will transfer all of your funds.