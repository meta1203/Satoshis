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

Probable Commands:  

Base: /sat, /satoshis Usage:  

/sat - List current amount of Bitcoin in your minecraft account.  
/sat give <player> <amount> - Transfer's money from your account to the selected player's account  
/sat req <player> <amount> - Requests money from another player. Does not guarantee that you will receive the money.  
/sat withdraw <address> [amount] - Transfers money from your account to your Bitcoin wallet. Must have at least the amount specified in the config. If the amount is left off, it will transfer all of your funds.  