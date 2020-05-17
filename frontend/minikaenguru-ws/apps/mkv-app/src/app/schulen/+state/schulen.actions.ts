import { createAction, props } from '@ngrx/store';



export const loadAllCourses = createAction(
    "[Schulen Resolver] load teilnahmenummern"
);


export const allCoursesLoaded = createAction(
    "[Load Courses Effect] All Courses Loaded",
    props<{courses: string[]}>()
);
