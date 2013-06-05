Satoshis
========

Minecraft with real money!  

Ah, economy plugins... Without them, we would have no in game currency! But what if this in game currency... was also out-of-game currency?  

*Enter Satoshis!*
Coupled with the Bitcoin P2P digital currency, you can trade in game with real money!

The design is simple:  
1) Player starts with no money  
2) The player can add money to their account by sending Bitcoin to a certain address.  
3) The player can withdraw money from their account to any Bitcoin address.  
4) Trade takes place as usual, as the plugin links into vault. Any shop/purchase plugin supporting vault is supported here!  
5) Tax system allows for a "Sales Tax" on each money transfer. The settings for whether the buyer or the seller is held responsible for tax, and for tax rates, are available in the config.  

Commands:  

/money - List current amount of Bitcoin in your minecraft account.  
/transact <player> <amount> - Transfer's money from your account to the selected player's account.  
/deposit - Get a Bitcoin address to send a deposit to. The next transaction to that address will fund your account.  
/withdraw <address> [amount] - Transfers money from your account to your Bitcoin wallet. Must have at least the amount specified in the config. If the amount is left off, it will transfer all of your funds.  
/admin info - Print basic debuging and economy info.  
/admin reset - Delete and re-download the block chain.  
/syscheck - Verify that the current Bitcoin holdings tally with the balances of all in-game accounts.  
/credit <player> <amount> - Add the specified amount to the given player's balance  
/debit <player> <amount> - Subtract the specified amount from the given player's balance  

Jenkins: http://play.metaserve.net:8080/

Permissions:
satoshis.* - All commands  
satoshis.money - /money  
satoshis.transact - /transact  
satoshis.withdraw - /withdraw  
satoshis.admin - /admin  
satoshis.info - /syscheck  
satoshis.credit - /credit  
satoshis.debit - /debit
