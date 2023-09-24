SELECT players.uuid, tpenabled, isvanished, balances.balance
    FROM players LEFT JOIN balances ON players.uuid = balances.uuid;