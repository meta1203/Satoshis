Dogecoins!
========

Minecraft with dogecoins!  

Ah, economy plugins... Without them, we would have no in game currency! But what if this in game currency... was also out-of-game currency? 
What's more, Dogecoins are family friendly! no buying nasty stuff, it's the internet currency that is safe and funny!

*Enter Dogecoins!*
Coupled with the Dogecoin P2P digital currency, you can trade in game with real money!

The design is simple:  
1) Player starts with no money  
2) The player can add money to their account by sending Dogecoins to a certain address.  
3) The player can withdraw money from their account to any Dogecoin address.  
4) Trade takes place as usual, as the plugin links into vault. Any shop/purchase plugin supporting vault is supported here!  
5) Tax system allows for a "Sales Tax" on each money transfer. The settings for whether the buyer or the seller is held responsible for tax, and for tax rates, are available in the config.  

Commands:  

/money - List current amount of Dogecoins in your minecraft account.  
/transact <player> <amount> - Transfer's money from your account to the selected player's account.  
/deposit - Get a Bitcoin address to send a deposit to. The next transaction to that address will fund your account.  
/withdraw <address> [amount] - Transfers money from your account to your Dogecoin wallet. Must have at least the amount specified in the config. If the amount is left off, it will transfer all of your funds.  
/admin info - Print basic debugging and economy info.  
/admin reset - Delete and re-download the block chain.  
/syscheck - Verify that the current Dogecoin holdings tally with the balances of all in-game accounts.  
/credit <player> <amount> - Add the specified amount to the given player's balance  
/debit <player> <amount> - Subtract the specified amount from the given player's balance  

Permissions:  
dogecoins.* - All commands  
dogecoins.money - /money  
dogecoins.transact - /transact  
dogecoins.withdraw - /withdraw  
dogecoins.admin - /admin  
dogecoins.info - /syscheck  
dogecoins.credit - /credit  
dogecoins.debit - /debit  
