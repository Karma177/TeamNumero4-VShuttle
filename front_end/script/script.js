/**
 * Scambia la visibilità tra il div `#action` e il div `#controlli`.
 * Se viene passato `true` esplicito mostra il `#action` e nasconde i controlli.
 * Se viene passato `false` fa l'opposto. Se non viene passato alcun argomento
 * esegue un toggle basato sull'attuale stato (utile da console).
 *
 * @param {boolean} [mostraAction] - Facoltativo. Se true mostra GO, se false mostra
 *                                   i pulsanti manuali. Se omesso viene invertito lo stato corrente.
 */
function toggleVisibility(mostraAction) {
    const actionElement = document.getElementById('action');
    const controlliElement = document.getElementById('controlli');

    // se non è specificato, inverti lo stato corrente
    if (typeof mostraAction === 'undefined') {
        mostraAction = actionElement.classList.contains('hidden');
    }

    if (mostraAction) {
        // mostra il "GO" e nasconde i pulsanti di override
        actionElement.classList.remove('hidden');
        controlliElement.classList.add('hidden');
    } else {
        // nasconde il "GO" e mostra i pulsanti di override
        actionElement.classList.add('hidden');
        controlliElement.classList.remove('hidden');
    }
    console.log('action classes:', actionElement.classList);
    console.log('controlli classes:', controlliElement.classList);
}


