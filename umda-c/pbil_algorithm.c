#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>
#include <gsl/gsl_rng.h>

//an individual in the population
struct ind{
	int *val;  	//bit-string as a 1-d array
	int fitness;
	struct ind *next;
};

int pbil(float eta, int lmbda, int mu, int n, int optimal_fitness, 
		int id, int func, int elite_size, int model_print, int var_print, int kj_print);
struct ind *create_ind(int *val, int fitness);
struct ind *add_ind(struct ind *head, struct ind *newind);
struct ind *sort_pop(struct ind *x);
struct ind *find_middle(struct ind *x);
struct ind *merge(struct ind *x, struct ind *y);
int evaluator(int *val, int n, int func);
int *array_sum(int *arr1, int *arr2, int n);
void free_ind(struct ind *p);
void print_ind(int *val, int n);
void print_p(float *p, int n);
void print_model(int iter, int best, float *p, int n);
int max(int a, int b);


int pbil(float eta, int lmbda, int mu, int n, int optimal_fitness, int id, int func, 
		int elite_size, int model_print, int var_print, int kj_print){
	float p[n];  	 //model representation ~ array
	int i,j;
	
	float up_border = 1.0-1.0/n;
	float low_border = 1.0/n;
	
	// random number generator	
	const gsl_rng_type * T;
  	gsl_rng * r;
	gsl_rng_env_setup();
  	T = gsl_rng_default;
 	r = gsl_rng_alloc (T);
 	gsl_rng_set(r, (unsigned long) n*id*time(NULL));

	for (i=0; i<n; i++){    //initial model
		p[i] = 0.5; 
	}
	
	struct ind *head = NULL;	
	
	for (i=0; i<lmbda; i++){  //sample initial population		
		int *val = (int *)malloc(n*sizeof(int));
		for (j=0; j<n; j++){			
			float u = gsl_rng_uniform (r);
			val[j] = (u <= p[j])?1:0;		
		}
		int fitness = evaluator(val, n, func);
		head = add_ind(head, create_ind(val,fitness));
	}	
	
	int iterations = 0; 
	struct ind *curr = NULL;
	struct ind *last_ind = NULL;  //the mu-th element in pop.
	int *sum_arr = (int *)malloc(n*sizeof(int)
);

	while(1){		
		iterations++;

		int k = 0;
		int curr_level = 0;
		int count;
			
		head = sort_pop(head);  //sort population

		last_ind = head;
		for (count=1; count <= mu; count++){
			last_ind = last_ind->next;
		} 
		curr_level = evaluator(last_ind->val,n,func)+1; //gamma_0 = mu/lambda

		int t = mu; 
		float var_k = 0;
		float var_k1 = 0;
		float var_k2 = 0;		
			
		for (i=0; i<n; i++){  //initialise the sum array  
			sum_arr[i] = 0;	
		}	
		int best_fit = head->fitness;	//best fitness value
		
		if (model_print==1){  
			print_model(iterations, best_fit, p, n);	
		}

		for (curr=head; t>0; curr=curr->next, t--){
			sum_arr = array_sum(sum_arr, curr->val, n);
		}
			
		for (i=0; i<n; i++){  //update model
			int x_i = sum_arr[i];
			p[i] = (1-eta)*p[i] + (eta*x_i)/mu;  
			if (p[i] < low_border){   
				p[i] = low_border;
			} else if (p[i] > up_border){ 
				p[i] = up_border;
			}
		}
		
		if (var_print==1){  //print var_k
			printf("%.5f \t %.5f \t %.5f \n",var_k, var_k1, var_k2); 
		}	
		
		if (kj_print==1){
			printf("%d \t %d \n", k, curr_level);
		}

		curr = head;  //reset curr
		for (i=1; i<=elite_size; i++){  //keep elite_size top inds
			curr = curr->next;
		}	

		if (best_fit == optimal_fitness){   //terminate?
                        break;
                }
		
		int x;
		while (curr != NULL){  //sample new population
			for (x=0; x<n; x++){					
				float u = gsl_rng_uniform (r);				
				curr->val[x] = (u <= p[x])?1:0;
			}
			curr->fitness = evaluator(curr->val, n, func);
			curr = curr->next;
		}							
	}
	free(sum_arr);
	free_ind(head);		
	return ((lmbda - elite_size)*iterations + elite_size); 
}

//print an individual
void print_ind(int *val, int n){
	int i;
	for (i=0;i<n;i++){
		printf("%d ",val[i]);
	}
	printf("\n");
}

//print the current probability model
void print_p(float *p, int n){
	int i;
	printf("Prob. model:\n");
	for (i=0;i<n;i++){
		printf("%.2f ",p[i]);
	}
	printf("\n");
}

//print the current model
void print_model(int iter, int best, float *p, int n){
	int i;
	printf("%d \t %d \t", iter, best);
	for (i=0; i<n; i++){
		printf("%f \t", p[i]);
	}
	printf("\n");
}

/**
//evaluate fitness value
int evaluator(int *val, int n, int func){
	int indx = 0;
	int ones = 0;	
	if (func==1) { //onemax
		while (indx<n){  
			ones += val[indx];
			indx++;
		}
	} else if (func==2){  //leadingones
		while ((val[indx] != 0) && (indx<n)){  
			ones++;
			indx++;
		}	
	} else if (func==3){ //binval
		while (indx<n){  
			if (val[indx]==1){ //consider ones only
				ones += 2^(n-indx-1);
			}
			indx++;
		}
	}	
	return ones;	
}*/


//evaluate leading-blocks
int evaluator(int *val, int n){
    int width = 2;
    int i, j;   //index
    int fitness = 0;
    for (i=0; i<n; i=i+width){ //only one loop
        if (val[i]==1 && val[i+1]==1){  //11
            fitness  = fitness + 3;
        } else if ((val[i]==0 && val[i+1]==1) || (val[i]==1 && val[i+1]==0)){  //01 and 10
            fitness = fitness + 1;
            break;
        } else {  //00
            fitness = fitness + 2;
            break;
        }
    }
    return fitness;
}


int *array_sum(int *arr1, int *arr2, int n){	
	int i;
	for (i=0;i<n;i++){
		arr1[i] = arr1[i] + arr2[i];
	}
	return arr1;
}

//create a dictionary
struct ind *create_ind(int *val, int fitness){
	struct ind *p = (struct ind *)malloc(sizeof(struct ind));
	if (p == NULL){
		return NULL;
	}
	p->val = val;
	p->fitness = fitness;
	p->next = NULL;
	return p;
}

//add a new dict into the current dict
struct ind *add_ind(struct ind *head, struct ind *new){
	if (head == NULL){
		return new;
	} 
	if (new != NULL){
		new->next = head;
		head = new;
	}
	return head;	
}

//free the memory
void free_ind(struct ind *head){
	struct ind *tmp;
	while (head != NULL){
		tmp = head;
		head = head->next;
		free(tmp->val);
		free(tmp);
	}	
}

//sort a dictionary
struct ind *sort_pop(struct ind *x){
	
	struct ind *y = NULL;
	struct ind *m = NULL;  
	
	if ( x == NULL || x->next == NULL ){
		return x;
	}	
	m = find_middle(x);
	y = m->next;
	m->next = NULL;
	return merge(sort_pop(x), sort_pop(y));
}

//find the middle elem in a dictionary
struct ind *find_middle(struct ind *x){
	
	struct ind *slow = x;
	struct ind *fast = x;
	
	while( fast->next != NULL && fast->next->next != NULL ){
		slow = slow->next;
		fast = fast->next->next;
	}
	return slow;
}

//merge two dicts 
struct ind *merge(struct ind *x, struct ind *y){

	struct ind *tmp  = NULL;
	struct ind *head = NULL;
	struct ind *curr = NULL;
  
	if( x == NULL ){
		head = y;
	}else if( y == NULL ){
		head = x;
	}else {   
		while( x != NULL && y != NULL ) {
			
			// Swap x and y if x is not largest.
			if( x->fitness < y->fitness ) {
				tmp = y;
				y   = x;
				x   = tmp;
			}
      
			if( head == NULL ) { // First element?
				head = x;
				curr = x;
			} else {
				curr->next = x;
				curr = curr->next;
			}
			x = x->next;
		}

		// Either x or y is empty.
		if( x != NULL ){
			curr->next = x;
		}else if( y != NULL ){
			curr->next = y; 
		}			     
	}
	return head;
}

//find the max between two ints
int max(int a, int b){
	return (a>=b)?a:b;
}


int main(int argc, char **argv){
	
	if (argc != 10){
		printf("USAGE: %s n func pop l_rate id elite_size print_model print_var_k print_kj\nwhere:\n\tn: length of bitstring\n\tfunc: 1 for OM, 2 for LO and 3 for BVAL\n\tpop: 1 for (sqrt(n),n), 2 for (sqrt(n)*log(n), n), and 3 for (n,n*log(n))\n\tl_rate: smoothing rate\n\telite_size=0\n\tprint_model, print_kj, print_var_k: 0 for F and 1 for T\n", argv[0]);
		exit(1);
	}	
	
	int n = atoi(argv[1]); 		 //problem size n
	int id = atoi(argv[5]); 	 //job id
	int func = atoi(argv[2]);	 //function
	int pop = atoi(argv[3]);  	//population
	float eta = atof(argv[4]) ;	 //learning rate
	int elite_size = atoi(argv[6]);  //elitism size
	int model_print = atoi(argv[7]); 
	int var_print = atoi(argv[8]);
	int kj_print = atoi(argv[9]); //print k-j
 
	int mu;
    float log2n = log10(n)/log10(2);
	
	if (pop==1){	 //small mu
		mu = (int) sqrt(n);   	//sqrt(n)
	} else if (pop==2){  	//medium mu
        mu = (int) sqrt(n)*log2n;  //sqrt(n)*log2(n)
	} else if (pop==3){		//large pop
		mu = (int) n;
	}
    
    	//float c = 2*exp(1.0)/(eta*eta);
    	//int lmbda = (int) c*mu;
    int lmbda = (int) 10*mu;  //lambda = 10mu
	
	int optimal_fitness;
	if (func==1 || func==2){  //onemax and leadingones
		optimal_fitness = n;
	} else if (func==3){ //binval
		int i;
		optimal_fitness = 0; 
		for (i=1; i<=n; i++){
			optimal_fitness += 2^(n-i);
		}
	}

	int res = pbil(eta, lmbda, mu, n, optimal_fitness, id, func, elite_size, model_print, var_print, kj_print);
	if (model_print==0 && var_print==0 && kj_print==0){
		printf("%d \n", res);
	}
	
	return(0);	
}

