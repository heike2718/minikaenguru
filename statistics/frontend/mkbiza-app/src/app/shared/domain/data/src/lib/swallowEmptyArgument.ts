/** 
 * Da die linting rules in den reducern mit nicht benutzren action-parametern ein Problem haben, machen wir halt hier etwas damit
 * Sollte immer so aufgerufen werden: swallowEmptyArgument(action, false)
*/
export function swallowEmptyArgument(argument: NonNullable<unknown>, doit: boolean): void {

    if (doit) {
        console.log(argument);
    }
};