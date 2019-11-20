#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <gsl/gsl_rng.h>
#include <time.h>
#include <stdbool.h>

//an individual in the population

struct ind {
    int *val; //bit-string as a 1-d array
    int fitness;
    struct ind *next;
};

int umda(int lmbda, int mu, int n, int optimal_fitness, int id, bool print_model);
struct ind *create_ind(int *val, int fitness);
struct ind *add_ind(struct ind *head, struct ind *newind);
struct ind *sort_pop(struct ind *x);
struct ind *find_middle(struct ind *x);
struct ind *merge(struct ind *x, struct ind *y);
int evaluator(int *val, int n);
int *array_sum(int *arr1, int *arr2, int n);
void model_printer(int iter, int best, float *p, int n);
void free_ind(struct ind *p);
void print_ind(int *val, int n);
void print_p(float *p, int n);
int max(int a, int b);

//umda implementation

int umda(int lmbda, int mu, int n, int optimal_fitness, int id, bool print_model) {
    float p[n]; //model representation ~ array
    int i, j;

    // random number generator	
    const gsl_rng_type * T;
    gsl_rng * r;
    gsl_rng_env_setup();
    T = gsl_rng_default;
    r = gsl_rng_alloc(T);
    gsl_rng_set(r, (unsigned long) n * id * time(NULL));

    for (i = 0; i < n; i++) { //initial model
        p[i] = 0.5;
    }
    struct ind *head = NULL;
    for (i = 0; i < lmbda; i++) { //sample initial population		
        int *val = (int *) malloc(n * sizeof (int));
        for (j = 0; j < n; j++) {
            float u = gsl_rng_uniform(r);
            val[j] = (u <= p[j]) ? 1 : 0;
        }
        int fitness = evaluator(val, n);
        head = add_ind(head, create_ind(val, fitness));
    }
    int iterations = 0;
    struct ind *curr = NULL;
    while (1) {
        iterations++;
        head = sort_pop(head); //sort population 

        int t = mu;
        int k = 0;
        int l = 0;

        int *arr = head->val;
        int best_fit = head->fitness; //best fitness value	

        if (print_model) {
            model_printer(iterations, best_fit, p, n);
        }

        if (best_fit == optimal_fitness) { //terminate?
            break;
        }
        for (curr = head->next; curr != NULL && t > 1; curr = curr->next, t--) {
            arr = array_sum(arr, curr->val, n);
        }
        for (i = 0; i < n; i++) { //update model
            int x_i = arr[i];
            if (x_i == 0) {
                p[i] = 1.0 / n;
            } else if (x_i == mu) {
                l++;
                p[i] = 1 - (1.0 / n);
            } else {
                k++;
                p[i] = ((float) x_i) / mu;
            }
        }
        curr = head; //reset curr
        int x;
        while (curr != NULL) { //sample new population
            for (x = 0; x < n; x++) {
                float u = gsl_rng_uniform(r);
                curr->val[x] = (u <= p[x]) ? 1 : 0;
            }
            curr->fitness = evaluator(curr->val, n);
            curr = curr->next;
        }
    }
    free_ind(head);
    return (lmbda * iterations);
}

//print the current model

void model_printer(int iter, int best, float *p, int n) {
    int i;
    printf("%d \t %d \t", iter, best);
    for (i = 0; i < n; i++) {
        printf("%f \t", p[i]);
    }
    printf("\n");
}

//print an individual

void print_ind(int *val, int n) {
    int i;
    for (i = 0; i < n; i++) {
        printf("%d ", val[i]);
    }
    printf("\n");
}

//print the current probability model

void print_p(float *p, int n) {
    int i;
    printf("Prob. model:\n");
    for (i = 0; i < n; i++) {
        printf("%.2f ", p[i]);
    }
    printf("\n");
}

//evaluate fitness value for onemax

int evaluator(int *val, int n) {
    int indx; //index
    int ones = 0;
    for (indx = 0; indx < n; indx++) {
        ones += val[indx];
    }
    return ones;
}

int *array_sum(int *arr1, int *arr2, int n) {
    int i;
    for (i = 0; i < n; i++) {
        arr1[i] = arr1[i] + arr2[i];
    }
    return arr1;
}

//create a dictionary

struct ind *create_ind(int *val, int fitness) {
    struct ind *p = (struct ind *) malloc(sizeof (struct ind));
    if (p == NULL) {
        return NULL;
    }
    p->val = val;
    p->fitness = fitness;
    p->next = NULL;
    return p;
}

//add a new dict into the current dict

struct ind *add_ind(struct ind *head, struct ind *new) {
    if (head == NULL) {
        return new;
    }
    if (new != NULL) {
        new->next = head;
        head = new;
    }
    return head;
}

//free the memory

void free_ind(struct ind *head) {
    struct ind *tmp;
    while (head != NULL) {
        tmp = head;
        head = head->next;
        free(tmp->val);
        free(tmp);
    }
}

//sort a dictionary

struct ind *sort_pop(struct ind *x) {

    struct ind *y = NULL;
    struct ind *m = NULL;

    if (x == NULL || x->next == NULL) {
        return x;
    }
    m = find_middle(x);
    y = m->next;
    m->next = NULL;
    return merge(sort_pop(x), sort_pop(y));
}

//find the middle elem in a dictionary

struct ind *find_middle(struct ind *x) {

    struct ind *slow = x;
    struct ind *fast = x;

    while (fast->next != NULL && fast->next->next != NULL) {
        slow = slow->next;
        fast = fast->next->next;
    }
    return slow;
}

//merge two dicts 

struct ind *merge(struct ind *x, struct ind *y) {

    struct ind *tmp = NULL;
    struct ind *head = NULL;
    struct ind *curr = NULL;

    if (x == NULL) {
        head = y;
    } else if (y == NULL) {
        head = x;
    } else {
        while (x != NULL && y != NULL) {

            // Swap x and y if x is not largest.
            if (x->fitness < y->fitness) {
                tmp = y;
                y = x;
                x = tmp;
            }

            if (head == NULL) { // First element?
                head = x;
                curr = x;
            } else {
                curr->next = x;
                curr = curr->next;
            }
            x = x->next;
        }

        // Either x or y is empty.
        if (x != NULL) {
            curr->next = x;
        } else if (y != NULL) {
            curr->next = y;
        }
    }
    return head;
}

//find the max between two ints

int max(int a, int b) {
    return (a >= b) ? a : b;
}

int main(int argc, char **argv) {

    if (argc != 5) {
        printf("USAGE: %s n id config(1/2) print_model(0/1) \n", argv[0]);
        exit(1);
    }

    int n = atoi(argv[1]); //problem size n
    int id = atoi(argv[2]); //job id
    int config = atoi(argv[3]); //config	
    bool print_model = (atoi(argv[4]) != 0);

    int lmbda;
    int mu;

    if (config == 1) { //small mu
        mu = (int) sqrt(n); // mu=sqrt(n)
        lmbda = (int) n;
    } else if (config == 2) { //large mu
        lmbda = (int) n;
        mu = (int) sqrt(n)*(log10(n) / log10(2)); //mu = sqrt(n)*log2(n)	
    }

    int optimal_fitness = n;
    int res = umda(lmbda, mu, n, optimal_fitness, id, print_model);
    if (!print_model) {
        printf("%d \n", res);
    }
    return (0);
}
